use crate::ops::elementwise::compare::r#where;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_where_d0_to_d0(
    condition: *const CPUBuffer,
    x: f32,
    y: f32,
    result: *mut CPUBuffer,
) {
    let condition = unsafe { &*condition };
    let result = unsafe { &mut *result };
    r#where::where_d0_to_d0(condition, x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_where_d0_to_d1(
    condition: *const CPUBuffer,
    x: f32,
    y: *const CPUBuffer,
    result: *mut CPUBuffer,
) {
    let condition = unsafe { &*condition };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    r#where::where_d0_to_d1(condition, x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_where_d1_to_d0(
    condition: *const CPUBuffer,
    x: *const CPUBuffer,
    y: f32,
    result: *mut CPUBuffer,
) {
    let condition = unsafe { &*condition };
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    r#where::where_d1_to_d0(condition, x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn com_wsr_cpu_where_d1_to_d1(
    condition: *const CPUBuffer,
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    result: *mut CPUBuffer,
) {
    let condition = unsafe { &*condition };
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    r#where::where_d1_to_d1(condition, x, y, result);
}
