use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_random(
    from: f32,
    until: f32,
    seed: i64,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::generator::random_d1(from as f32, until as f32, seed as u32, result, runtime);
}
