use crate::{resource::buffer::GPUBuffer, runtime::Runtime};

pub fn transpose_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.shape.transpose_d2(x, xi, xj, result);
    runtime.dispatch(task);
}

pub fn transpose_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    axis_i: usize,
    axis_j: usize,
    axis_k: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match (axis_i, axis_j, axis_k) {
        (0, 1, 2) => {
            drop(_t);
            copy_into_d1(x, &result, 0, x.count(), 1, runtime);
            return;
        }
        (1, 2, 0) => runtime.kernels.shape.transpose_d2(x, xi, xj * xk, result),
        (2, 0, 1) => runtime.kernels.shape.transpose_d2(x, xi * xj, xk, result),
        _ => runtime
            .kernels
            .shape
            .transpose_d3(x, xi, xj, xk, axis_i, axis_j, axis_k, result),
    };
    runtime.dispatch(task);
}

pub fn transpose_d4(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    axis_i: usize,
    axis_j: usize,
    axis_k: usize,
    axis_l: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match (axis_i, axis_j, axis_k, axis_l) {
        (0, 1, 2, 3) => {
            drop(_t);
            copy_into_d1(x, &result, 0, x.count(), 1, runtime);
            return;
        }
        (1, 2, 3, 0) => runtime
            .kernels
            .shape
            .transpose_d2(x, xi, xj * xk * xl, result),
        (2, 3, 0, 1) => runtime
            .kernels
            .shape
            .transpose_d2(x, xi * xj, xk * xl, result),
        (3, 0, 1, 2) => runtime
            .kernels
            .shape
            .transpose_d2(x, xi * xj * xk, xl, result),
        _ => runtime
            .kernels
            .shape
            .transpose_d4(x, xi, xj, xk, xl, axis_i, axis_j, axis_k, axis_l, result),
    };
    runtime.dispatch(task);
}

pub fn slice_d1(
    x: &GPUBuffer,
    start: usize,
    end: usize,
    step: isize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.shape.slice_d1(x, start, end, step, result);
    runtime.dispatch(task);
}

pub fn slice_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime
            .kernels
            .shape
            .slice_d2_axis0(x, xi, xj, start, end, step, result),
        _ => runtime
            .kernels
            .shape
            .slice_d2_axis1(x, xi, xj, start, end, step, result),
    };
    runtime.dispatch(task);
}

pub fn slice_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime
            .kernels
            .shape
            .slice_d2_axis0(x, xi, xj * xk, start, end, step, result),
        1 => runtime
            .kernels
            .shape
            .slice_d3(x, xi, xj, xk, start, end, step, result),
        _ => runtime
            .kernels
            .shape
            .slice_d2_axis1(x, xi * xj, xk, start, end, step, result),
    };
    runtime.dispatch(task);
}

pub fn copy_into_d1(
    x: &GPUBuffer,
    result: &GPUBuffer,
    start: usize,
    end: usize,
    step: isize,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .shape
        .copy_into_d1(x, result, start, end, step);
    runtime.dispatch(task);
}

pub fn copy_into_d2(
    x: &GPUBuffer,
    result: &GPUBuffer,
    ri: usize,
    rj: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime
            .kernels
            .shape
            .copy_into_d2_axis0(x, result, ri, rj, start, end, step),
        _ => runtime
            .kernels
            .shape
            .copy_into_d2_axis1(x, result, ri, rj, start, end, step),
    };
    runtime.dispatch(task);
}

pub fn copy_into_d3(
    x: &GPUBuffer,
    result: &GPUBuffer,
    ri: usize,
    rj: usize,
    rk: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime
            .kernels
            .shape
            .copy_into_d2_axis0(x, result, ri, rj * rk, start, end, step),
        1 => runtime
            .kernels
            .shape
            .copy_into_d3(x, result, ri, rj, rk, start, end, step),
        _ => runtime
            .kernels
            .shape
            .copy_into_d2_axis1(x, result, ri * rj, rk, start, end, step),
    };
    runtime.dispatch(task);
}

pub fn flip_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime.kernels.shape.flip_d2_axis0(x, xi, xj * xk, result),
        1 => runtime.kernels.shape.flip_d3(x, xi, xj, xk, result),
        _ => runtime.kernels.shape.flip_d2_axis1(x, xi * xj, xk, result),
    };
    runtime.dispatch(task);
}

pub fn unfold_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    b: usize,
    window: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .shape
        .unfold_d1(x, xi, xj, b, window, stride, dilation, padding, result);
    runtime.dispatch(task);
}

pub fn unfold_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    b: usize,
    window: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .shape
        .unfold_d2(x, xi, xj, xk, b, window, stride, dilation, padding, result);
    runtime.dispatch(task);
}

pub fn fold_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    b: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .shape
        .fold_d1(x, xi, xj, xk, b, stride, dilation, padding, result);
    runtime.dispatch(task);
}

pub fn fold_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    b: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .shape
        .fold_d2(x, xi, xj, xk, xl, b, stride, dilation, padding, result);
    runtime.dispatch(task);
}
