use std::sync::Arc;

use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_buffer_allocate(
    size: i32,
    runtime: *const Runtime,
) -> *const GPUBuffer {
    let runtime = unsafe { &*runtime };
    let buffer = ops::buffer::create(size as usize, runtime);
    Arc::into_raw(Arc::new(buffer))
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_buffer_init(
    value: *const f32,
    size: i32,
    runtime: *const Runtime,
) -> *const GPUBuffer {
    let value = unsafe { std::slice::from_raw_parts(value, size as usize) };
    let runtime = unsafe { &*runtime };
    let buffer = ops::buffer::init(value, runtime);
    Arc::into_raw(Arc::new(buffer))
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_buffer_release(ptr: *const GPUBuffer, runtime: *const Runtime) {
    let buffer = unsafe { Arc::from_raw(ptr) };
    let runtime = unsafe { &*runtime };
    if let Ok(buffer) = Arc::try_unwrap(buffer) {
        ops::buffer::release(buffer, runtime);
    }
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_buffer_read_all(
    ptr: *const GPUBuffer,
    runtime: *mut Runtime,
    result: *mut f32,
    size: i32,
) {
    let buffer = unsafe { &*ptr };
    let runtime = unsafe { &mut *runtime };
    let values = ops::buffer::read_all(buffer, runtime);
    let out = unsafe { std::slice::from_raw_parts_mut(result, size as usize) };
    out.copy_from_slice(&values);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_buffer_write(
    ptr: *const GPUBuffer,
    index: i32,
    value: f32,
    runtime: *mut Runtime,
) {
    let buffer = unsafe { &*ptr };
    let runtime = unsafe { &mut *runtime };
    ops::buffer::write(buffer, index as usize, value, runtime);
}
