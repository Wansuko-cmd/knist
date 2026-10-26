use crate::{resource::buffer::GPUBuffer, runtime::Runtime};

pub fn gather(
    x: &GPUBuffer,
    y: &GPUBuffer,
    i: usize,
    j: usize,
    k: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.index.gather(x, y, i, j, k, result);
    runtime.dispatch(task);
}

pub fn scatter_add(
    x: &GPUBuffer,
    y: &GPUBuffer,
    i: usize,
    j: usize,
    k: usize,
    b: usize,
    result: &GPUBuffer,
    runtime: &mut Runtime,
) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime.kernels.index.scatter_add(x, y, i, j, k, b, result);
    runtime.dispatch(task);
}
