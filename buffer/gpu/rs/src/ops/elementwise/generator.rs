use crate::{resource::buffer::GPUBuffer, runtime::Runtime};

pub fn random_d1(from: f32, until: f32, seed: u32, result: &GPUBuffer, runtime: &mut Runtime) {
    let _t = runtime.cpu_profiler.start();
    let task = runtime
        .kernels
        .generator
        .random_d1(from, until, seed, result);
    runtime.dispatch(task);
}
