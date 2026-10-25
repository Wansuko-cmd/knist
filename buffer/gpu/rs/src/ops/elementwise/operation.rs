use crate::{resource::buffer::GPUBuffer, runtime::Runtime};

pub fn plus_with_d0_to_d1(x: f32, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.plus_d0_to_d1(x, y, result);
    runtime.dispatch(task);
}

pub fn plus_with_d1_to_d0(x: &GPUBuffer, y: f32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.plus_d1_to_d0(x, y, result);
    runtime.dispatch(task);
}

pub fn plus_with_d1_to_d1(x: &GPUBuffer, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, 1, result.count() as u32];
    let x_stride = [0, 0, 0, 1];
    let y_stride = [0, 0, 0, 1];

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d1_to_d2(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, yi, yj].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, 0, yj, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d1_to_d3(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d2_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, xi, xj].map(|v| v as u32);
    let x_stride = [0, 0, xj, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d2_to_d3(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match (axis1, axis2) {
        (0, 1) => [0, xj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, xj, 0, 1].map(|v| v as u32),
        _ => [0, 0, xj, 1].map(|v| v as u32),
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d3_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d3_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [0, yj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d3_to_d4(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    yl: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [yi, yj, yk, yl].map(|v| v as u32);
    let x_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [xj * xk, xk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [xj * xk, xk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [xj * xk, 0, xk, 1].map(|v| v as u32),
        _ => [0, xj * xk, xk, 1].map(|v| v as u32),
    };
    let y_stride = [yj * yk * yl, yk * yl, yl, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d4_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [1, 0, 0, 0],
        1 => [0, 1, 0, 0],
        2 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d4_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [yj, 1, 0, 0].map(|v| v as u32),
        (0, 2) => [yj, 0, 1, 0].map(|v| v as u32),
        (0, 3) => [yj, 0, 0, 1].map(|v| v as u32),
        (1, 2) => [0, yj, 1, 0].map(|v| v as u32),
        (1, 3) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn plus_with_d4_to_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [yj * yk, yk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [yj * yk, yk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [yj * yk, 0, yk, 1].map(|v| v as u32),
        _ => [0, yj * yk, yk, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .plus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d0_to_d1(x: f32, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.minus_d0_to_d1(x, y, result);
    runtime.dispatch(task);
}

pub fn minus_with_d1_to_d0(x: &GPUBuffer, y: f32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.minus_d1_to_d0(x, y, result);
    runtime.dispatch(task);
}

pub fn minus_with_d1_to_d1(
    x: &GPUBuffer,
    y: &GPUBuffer,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, 1, result.count() as u32];
    let x_stride = [0, 0, 0, 1];
    let y_stride = [0, 0, 0, 1];

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d1_to_d2(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, yi, yj].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, 0, yj, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d1_to_d3(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d2_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, xi, xj].map(|v| v as u32);
    let x_stride = [0, 0, xj, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d2_to_d3(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match (axis1, axis2) {
        (0, 1) => [0, xj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, xj, 0, 1].map(|v| v as u32),
        _ => [0, 0, xj, 1].map(|v| v as u32),
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d3_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d3_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [0, yj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d3_to_d4(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    yl: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [yi, yj, yk, yl].map(|v| v as u32);
    let x_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [xj * xk, xk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [xj * xk, xk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [xj * xk, 0, xk, 1].map(|v| v as u32),
        _ => [0, xj * xk, xk, 1].map(|v| v as u32),
    };
    let y_stride = [yj * yk * yl, yk * yl, yl, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d4_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [1, 0, 0, 0],
        1 => [0, 1, 0, 0],
        2 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d4_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [yj, 1, 0, 0].map(|v| v as u32),
        (0, 2) => [yj, 0, 1, 0].map(|v| v as u32),
        (0, 3) => [yj, 0, 0, 1].map(|v| v as u32),
        (1, 2) => [0, yj, 1, 0].map(|v| v as u32),
        (1, 3) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn minus_with_d4_to_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [yj * yk, yk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [yj * yk, yk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [yj * yk, 0, yk, 1].map(|v| v as u32),
        _ => [0, yj * yk, yk, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .minus_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d0_to_d1(x: f32, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.times_d0_to_d1(x, y, result);
    runtime.dispatch(task);
}

pub fn times_with_d1_to_d0(x: &GPUBuffer, y: f32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.times_d1_to_d0(x, y, result);
    runtime.dispatch(task);
}

pub fn times_with_d1_to_d1(
    x: &GPUBuffer,
    y: &GPUBuffer,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, 1, result.count() as u32];
    let x_stride = [0, 0, 0, 1];
    let y_stride = [0, 0, 0, 1];

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d1_to_d2(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, yi, yj].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, 0, yj, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d1_to_d3(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d2_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, xi, xj].map(|v| v as u32);
    let x_stride = [0, 0, xj, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d2_to_d3(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match (axis1, axis2) {
        (0, 1) => [0, xj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, xj, 0, 1].map(|v| v as u32),
        _ => [0, 0, xj, 1].map(|v| v as u32),
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d3_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d3_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [0, yj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d3_to_d4(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    yl: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [yi, yj, yk, yl].map(|v| v as u32);
    let x_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [xj * xk, xk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [xj * xk, xk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [xj * xk, 0, xk, 1].map(|v| v as u32),
        _ => [0, xj * xk, xk, 1].map(|v| v as u32),
    };
    let y_stride = [yj * yk * yl, yk * yl, yl, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d4_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [1, 0, 0, 0],
        1 => [0, 1, 0, 0],
        2 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d4_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [yj, 1, 0, 0].map(|v| v as u32),
        (0, 2) => [yj, 0, 1, 0].map(|v| v as u32),
        (0, 3) => [yj, 0, 0, 1].map(|v| v as u32),
        (1, 2) => [0, yj, 1, 0].map(|v| v as u32),
        (1, 3) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn times_with_d4_to_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [yj * yk, yk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [yj * yk, yk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [yj * yk, 0, yk, 1].map(|v| v as u32),
        _ => [0, yj * yk, yk, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .times_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d0_to_d1(x: f32, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.div_d0_to_d1(x, y, result);
    runtime.dispatch(task);
}

pub fn div_with_d1_to_d0(x: &GPUBuffer, y: f32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.operation.div_d1_to_d0(x, y, result);
    runtime.dispatch(task);
}

pub fn div_with_d1_to_d1(x: &GPUBuffer, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, 1, result.count() as u32];
    let x_stride = [0, 0, 0, 1];
    let y_stride = [0, 0, 0, 1];

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d1_to_d2(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, yi, yj].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, 0, yj, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d1_to_d3(
    x: &GPUBuffer,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d2_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, 1, xi, xj].map(|v| v as u32);
    let x_stride = [0, 0, xj, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d2_to_d3(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, yi, yj, yk].map(|v| v as u32);
    let x_stride = match (axis1, axis2) {
        (0, 1) => [0, xj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, xj, 0, 1].map(|v| v as u32),
        _ => [0, 0, xj, 1].map(|v| v as u32),
    };
    let y_stride = [0, yj * yk, yk, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d3_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [0, 1, 0, 0],
        1 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d3_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [1, xi, xj, xk].map(|v| v as u32);
    let x_stride = [0, xj * xk, xk, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [0, yj, 1, 0].map(|v| v as u32),
        (0, 2) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d3_to_d4(
    x: &GPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    y: &GPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    yl: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [yi, yj, yk, yl].map(|v| v as u32);
    let x_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [xj * xk, xk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [xj * xk, xk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [xj * xk, 0, xk, 1].map(|v| v as u32),
        _ => [0, xj * xk, xk, 1].map(|v| v as u32),
    };
    let y_stride = [yj * yk * yl, yk * yl, yl, 1].map(|v| v as u32);

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d4_to_d1(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    axis: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match axis {
        0 => [1, 0, 0, 0],
        1 => [0, 1, 0, 0],
        2 => [0, 0, 1, 0],
        _ => [0, 0, 0, 1],
    };

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d4_to_d2(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2) {
        (0, 1) => [yj, 1, 0, 0].map(|v| v as u32),
        (0, 2) => [yj, 0, 1, 0].map(|v| v as u32),
        (0, 3) => [yj, 0, 0, 1].map(|v| v as u32),
        (1, 2) => [0, yj, 1, 0].map(|v| v as u32),
        (1, 3) => [0, yj, 0, 1].map(|v| v as u32),
        _ => [0, 0, yj, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}

pub fn div_with_d4_to_d3(
    x: &GPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &GPUBuffer,
    _yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let result_shape = [xi, xj, xk, xl].map(|v| v as u32);
    let x_stride = [xj * xk * xl, xk * xl, xl, 1].map(|v| v as u32);
    let y_stride = match (axis1, axis2, axis3) {
        (0, 1, 2) => [yj * yk, yk, 1, 0].map(|v| v as u32),
        (0, 1, 3) => [yj * yk, yk, 0, 1].map(|v| v as u32),
        (0, 2, 3) => [yj * yk, 0, yk, 1].map(|v| v as u32),
        _ => [0, yj * yk, yk, 1].map(|v| v as u32),
    };

    let task = runtime
        .kernels
        .operation
        .div_d4(x, y, result_shape, x_stride, y_stride, result);
    runtime.dispatch(task);
}
