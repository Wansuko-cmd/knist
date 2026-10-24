struct Params {
    xi: u32,
    xj: u32,
    xk: u32,
    b: u32,
    window: u32,
    stride: u32,
    dilation: u32,
    padding: u32,
}

@group(0) @binding(0) var<storage, read> x: array<f32>;
@group(0) @binding(1) var<storage, read_write> result: array<f32>;
var<immediate> params: Params;

@compute @workgroup_size(256, 1)
fn unfold_d2(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let rb = params.b;
    let ri = params.xi;
    let window_size = (params.window - 1) * params.dilation + 1;
    let rj = (params.xj - window_size + params.padding * 2) / params.stride + 1;
    let rk = (params.xk - window_size + params.padding * 2) / params.stride + 1;
    let rl = params.window * params.window;


    let result_index = id.y * num_groups.x * 256u + id.x;
    if (result_index >= rb * ri * rj * rk * rl) {
        return;
    }

    var tmp = result_index;
    let nl = tmp % rl; tmp /= rl;
    let nk = tmp % rk; tmp /= rk;
    let nj = tmp % rj; tmp /= rj;
    let ni = tmp % ri; tmp /= ri;
    let nb = tmp;

    let ob = nb;
    let oi = ni;
    let oj = nj * params.stride + (nl / params.window) * params.dilation - params.padding;
    let ok = nk * params.stride + (nl % params.window) * params.dilation - params.padding;
    if (oj < params.xj && ok < params.xk) {
        let x_index = ((ob * params.xi + oi) * params.xj + oj) * params.xk + ok;
        result[result_index] = x[x_index];
    }
}
