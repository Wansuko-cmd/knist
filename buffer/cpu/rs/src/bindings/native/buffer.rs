use crate::ops;
use crate::resource::buffer::CPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_buffer_allocate(
    size: i32,
    runtime: *const Runtime,
) -> *mut CPUBuffer {
    let runtime = unsafe { &*runtime };
    let buffer = ops::buffer::create(size as usize, runtime);
    Box::into_raw(Box::new(buffer))
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_buffer_init(
    value: *const f32,
    size: i32,
    runtime: *const Runtime,
) -> *mut CPUBuffer {
    let value = unsafe { std::slice::from_raw_parts(value, size as usize) };
    let runtime = unsafe { &*runtime };
    let buffer = ops::buffer::init(value, runtime);
    Box::into_raw(Box::new(buffer))
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_buffer_release(ptr: *mut CPUBuffer, runtime: *const Runtime) {
    let buffer = unsafe { Box::from_raw(ptr) };
    let runtime = unsafe { &*runtime };
    ops::buffer::release(*buffer, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_buffer_get(ptr: *const CPUBuffer, index: i32) -> f32 {
    let buffer = unsafe { &*ptr };
    buffer[index as usize]
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_buffer_set(ptr: *mut CPUBuffer, index: i32, value: f32) {
    let buffer = unsafe { &mut *ptr };
    buffer[index as usize] = value;
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_buffer_read_all(ptr: *const CPUBuffer, result: *mut f32) {
    let buffer = unsafe { &*ptr };
    let out = unsafe { std::slice::from_raw_parts_mut(result, buffer.count()) };
    out.copy_from_slice(buffer);
}
