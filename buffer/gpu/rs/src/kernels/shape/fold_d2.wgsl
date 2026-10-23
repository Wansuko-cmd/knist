struct Params {
    xi: u32,
    xj: u32,
    xk: u32,
    xl: u32,
    b: u32,
    window: u32,
    stride: u32,
    dilation: u32,
    padding: u32,
    _pad1: u32,
    _pad2: u32,
    _pad3: u32,
}

@group(0) @binding(0) var<storage, read> x: array<f32>;
@group(0) @binding(1) var<storage, read_write> result: array<atomic<u32>>;
var<immediate> params: Params;

@compute @workgroup_size(256, 1)
fn fold_d2(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let x_index = id.y * num_groups.x * 256u + id.x;
    if (x_index >= arrayLength(&x)) {
        return;
    }

    var tmp = x_index;
    let ol = tmp % params.xl; tmp /= params.xl;
    let ok = tmp % params.xk; tmp /= params.xk;
    let oj = tmp % params.xj; tmp /= params.xj;
    let oi = tmp % params.xi; tmp /= params.xi;
    let ob = tmp;

    let rb = params.b;
    let ri = params.xi;
    let window_size = (params.window - 1) * params.dilation + 1;
    let rj = window_size + (params.xj - 1) * params.stride - params.padding * 2;
    let rk = window_size + (params.xk - 1) * params.stride - params.padding * 2;

    let nb = ob;
    let ni = oi;
    let nj = oj * params.stride + (ol / params.window) * params.dilation - params.padding;
    let nk = ok * params.stride + (ol % params.window) * params.dilation - params.padding;
    if (nj < rj && nk < rk) {
        let result_index = ((nb * ri + ni) * rj + nj) * rk + nk;
        atomicAdd(result_index, x[x_index]);
    }
}

fn atomicAdd(index: u32, value: f32) {
    var old_bits = atomicLoad(&result[index]);
    loop {
        let old_value = bitcast<f32>(old_bits);
        let new_value = old_value + value;
        let new_bits = bitcast<u32>(new_value);
        
        let res = atomicCompareExchangeWeak(&result[index], old_bits, new_bits);
        if (res.exchanged) {
            break;
        }
        old_bits = res.old_value;
    }
}
