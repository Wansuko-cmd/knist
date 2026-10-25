pub mod r#where;

use crate::ops::elementwise::compare;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_greater_than_d1_to_d0(
    x: *const CPUBuffer,
    y: f32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    compare::greater_than_d1_to_d0(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_greater_than_d1_to_d1(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    compare::greater_than_d1_to_d1(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_less_than_d1_to_d0(
    x: *const CPUBuffer,
    y: f32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    compare::less_than_d1_to_d0(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_less_than_d1_to_d1(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    compare::less_than_d1_to_d1(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_equals_d1_to_d0(
    x: *const CPUBuffer,
    y: f32,
    atol: f32,
    rtol: f32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    compare::equals_d1_to_d0(x, y, atol, rtol, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_equals_d1_to_d1(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    atol: f32,
    rtol: f32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    compare::equals_d1_to_d1(x, y, atol, rtol, result);
}
