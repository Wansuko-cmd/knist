use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_greater_than_d1_to_d0(
    x: *const GPUBuffer,
    y: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::gt_d1_to_d0(x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_greater_than_d1_to_d1(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::gt_d1_to_d1(x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_less_than_d1_to_d0(
    x: *const GPUBuffer,
    y: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::lt_d1_to_d0(x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_less_than_d1_to_d1(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::lt_d1_to_d1(x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_equals_d1_to_d0(
    x: *const GPUBuffer,
    y: f32,
    atol: f32,
    rtol: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::eq_d1_to_d0(x, y, atol, rtol, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_equals_d1_to_d1(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    atol: f32,
    rtol: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::eq_d1_to_d1(x, y, atol, rtol, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_where_d0_to_d0(
    condition: *const GPUBuffer,
    x: f32,
    y: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let condition = unsafe { &*condition };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::where_d0_to_d0(condition, x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_where_d0_to_d1(
    condition: *const GPUBuffer,
    x: f32,
    y: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let condition = unsafe { &*condition };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::where_d0_to_d1(condition, x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_where_d1_to_d0(
    condition: *const GPUBuffer,
    x: *const GPUBuffer,
    y: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let condition = unsafe { &*condition };
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::where_d1_to_d0(condition, x, y, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_where_d1_to_d1(
    condition: *const GPUBuffer,
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let condition = unsafe { &*condition };
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::compare::where_d1_to_d1(condition, x, y, result, runtime);
}
