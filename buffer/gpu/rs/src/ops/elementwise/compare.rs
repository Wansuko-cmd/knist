use crate::{resource::buffer::GPUBuffer, runtime::Runtime};

pub fn gt_d1_to_d0(x: &GPUBuffer, y: f32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.compare.gt_d1_to_d0(x, y, result);
    runtime.dispatch(task);
}

pub fn gt_d1_to_d1(x: &GPUBuffer, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.compare.gt_d1_to_d1(x, y, result);
    runtime.dispatch(task);
}

pub fn lt_d1_to_d0(x: &GPUBuffer, y: f32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.compare.lt_d1_to_d0(x, y, result);
    runtime.dispatch(task);
}

pub fn lt_d1_to_d1(x: &GPUBuffer, y: &GPUBuffer, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.compare.lt_d1_to_d1(x, y, result);
    runtime.dispatch(task);
}

pub fn eq_d1_to_d0(
    x: &GPUBuffer,
    y: f32,
    atol: f32,
    rtol: f32,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .compare
        .eq_d1_to_d0(x, y, atol, rtol, result);
    runtime.dispatch(task);
}

pub fn eq_d1_to_d1(
    x: &GPUBuffer,
    y: &GPUBuffer,
    atol: f32,
    rtol: f32,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .compare
        .eq_d1_to_d1(x, y, atol, rtol, result);
    runtime.dispatch(task);
}

pub fn where_d0_to_d0(
    condition: &GPUBuffer,
    x: f32,
    y: f32,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .compare
        .where_d0_to_d0(condition, x, y, result);
    runtime.dispatch(task);
}

pub fn where_d0_to_d1(
    condition: &GPUBuffer,
    x: f32,
    y: &GPUBuffer,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .compare
        .where_d0_to_d1(condition, x, y, result);
    runtime.dispatch(task);
}

pub fn where_d1_to_d0(
    condition: &GPUBuffer,
    x: &GPUBuffer,
    y: f32,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .compare
        .where_d1_to_d0(condition, x, y, result);
    runtime.dispatch(task);
}

pub fn where_d1_to_d1(
    condition: &GPUBuffer,
    x: &GPUBuffer,
    y: &GPUBuffer,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .compare
        .where_d1_to_d1(condition, x, y, result);
    runtime.dispatch(task);
}
