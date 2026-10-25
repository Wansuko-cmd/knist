use crate::ops::index;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_gather(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    i: i32,
    j: i32,
    k: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    index::gather(x, y, i as usize, j as usize, k as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_scatter_add(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    i: i32,
    j: i32,
    k: i32,
    b: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    index::scatter_add(x, y, i as usize, j as usize, k as usize, b as usize, result);
}
