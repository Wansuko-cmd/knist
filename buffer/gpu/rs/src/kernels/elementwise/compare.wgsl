struct Params {
    x_val: f32,
    y_val: f32,
    atol: f32,
    rtol: f32,
}

@group(0) @binding(0) var<storage, read> condition: array<f32>;
@group(0) @binding(1) var<storage, read> x: array<f32>;
@group(0) @binding(2) var<storage, read> y: array<f32>;
@group(0) @binding(3) var<storage, read_write> result: array<f32>;
var<immediate> params: Params;

@compute @workgroup_size(256, 1)
fn gt_d1_to_d0(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    result[index] = select(0f, 1f, x[index] > params.y_val);
}

@compute @workgroup_size(256, 1)
fn gt_d1_to_d1(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    result[index] = select(0f, 1f, x[index] > y[index]);
}

@compute @workgroup_size(256, 1)
fn lt_d1_to_d0(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    result[index] = select(0f, 1f, x[index] < params.y_val);
}

@compute @workgroup_size(256, 1)
fn lt_d1_to_d1(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    result[index] = select(0f, 1f, x[index] < y[index]);
}

@compute @workgroup_size(256, 1)
fn eq_d1_to_d0(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    let tolerance = params.atol + params.rtol * abs(params.y_val);
    result[index] = select(0f, 1f, abs(x[index] - params.y_val) <= tolerance);
}

@compute @workgroup_size(256, 1)
fn eq_d1_to_d1(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    let tolerance = params.atol + params.rtol * abs(y[index]);
    result[index] = select(0f, 1f, abs(x[index] - y[index]) <= tolerance);
}

@compute @workgroup_size(256, 1)
fn where_d0_to_d0(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
   result[index] = select(params.y_val, params.x_val, condition[index] > 0.0);
}

@compute @workgroup_size(256, 1)
fn where_d0_to_d1(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
   result[index] = select(y[index], params.x_val, condition[index] > 0.0);
}

@compute @workgroup_size(256, 1)
fn where_d1_to_d0(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    result[index] = select(params.y_val, x[index], condition[index] > 0.0);
}

@compute @workgroup_size(256, 1)
fn where_d1_to_d1(@builtin(global_invocation_id) id: vec3<u32>, @builtin(num_workgroups) num_groups: vec3<u32>) {
    let stride = num_groups.x * 256;
    let index = id.y * stride + id.x;
    if (index >= arrayLength(&result)) {
        return;
    }
    result[index] = select(y[index], x[index], condition[index] > 0.0);
}
