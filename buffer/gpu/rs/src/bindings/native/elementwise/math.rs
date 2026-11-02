use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_exp(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::math::exp_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_ln(
    x: *const GPUBuffer,
    e: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::math::ln_d1(x, e as f32, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_sigmoid(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::math::sigmoid_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_pow(
    x: *const GPUBuffer,
    n: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::math::pow_d1(x, n as i32, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_sqrt(
    x: *const GPUBuffer,
    e: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::math::sqrt_d1(x, e as f32, result, runtime);
}
