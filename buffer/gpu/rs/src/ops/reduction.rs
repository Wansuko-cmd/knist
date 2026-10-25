use crate::{resource::buffer::GPUBuffer, runtime::Runtime};

pub fn average_d1(x: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.reduction.average_d1(x, result);
    runtime.dispatch(task);
}

pub fn average_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime
            .kernels
            .reduction
            .average_d2_axis0(x, xi, xj, result),
        _ => runtime
            .kernels
            .reduction
            .average_d2_axis1(x, xi, xj, result),
    };
    runtime.dispatch(task);
}

pub fn average_d3(
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
        0 => runtime
            .kernels
            .reduction
            .average_d2_axis0(x, xi, xj * xk, result),
        1 => runtime.kernels.reduction.average_d3(x, xi, xj, xk, result),
        _ => runtime
            .kernels
            .reduction
            .average_d2_axis1(x, xi * xj, xk, result),
    };
    runtime.dispatch(task);
}

pub fn max_d1(x: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.reduction.max_d1(x, result);
    runtime.dispatch(task);
}

pub fn max_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime.kernels.reduction.max_d2_axis0(x, xi, xj, result),
        _ => runtime.kernels.reduction.max_d2_axis1(x, xi, xj, result),
    };
    runtime.dispatch(task);
}

pub fn max_d3(
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
        0 => runtime
            .kernels
            .reduction
            .max_d2_axis0(x, xi, xj * xk, result),
        1 => runtime.kernels.reduction.max_d3(x, xi, xj, xk, result),
        _ => runtime
            .kernels
            .reduction
            .max_d2_axis1(x, xi * xj, xk, result),
    };
    runtime.dispatch(task);
}

pub fn min_d1(x: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.reduction.min_d1(x, result);
    runtime.dispatch(task);
}

pub fn min_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime.kernels.reduction.min_d2_axis0(x, xi, xj, result),
        _ => runtime.kernels.reduction.min_d2_axis1(x, xi, xj, result),
    };
    runtime.dispatch(task);
}

pub fn min_d3(
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
        0 => runtime
            .kernels
            .reduction
            .min_d2_axis0(x, xi, xj * xk, result),
        1 => runtime.kernels.reduction.min_d3(x, xi, xj, xk, result),
        _ => runtime
            .kernels
            .reduction
            .min_d2_axis1(x, xi * xj, xk, result),
    };
    runtime.dispatch(task);
}

pub fn sum_d1(x: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.reduction.sum_d1(x, result);
    runtime.dispatch(task);
}

pub fn sum_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = match axis {
        0 => runtime.kernels.reduction.sum_d2_axis0(x, xi, xj, result),
        _ => runtime.kernels.reduction.sum_d2_axis1(x, xi, xj, result),
    };
    runtime.dispatch(task);
}

pub fn sum_d3(
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
        0 => runtime
            .kernels
            .reduction
            .sum_d2_axis0(x, xi, xj * xk, result),
        1 => runtime.kernels.reduction.sum_d3(x, xi, xj, xk, result),
        _ => runtime
            .kernels
            .reduction
            .sum_d2_axis1(x, xi * xj, xk, result),
    };
    runtime.dispatch(task);
}
