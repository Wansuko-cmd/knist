use crate::ops::linalg;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_inner(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    b: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    linalg::inner(x, y, b as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_mat_mul_d1_to_d2(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    trans_y: bool,
    n: i32,
    k: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    linalg::mat_mul_d1_to_d2(x, y, trans_y, n as usize, k as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_mat_mul_d2_to_d1(
    x: *const CPUBuffer,
    trans_x: bool,
    y: *const CPUBuffer,
    m: i32,
    k: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    linalg::mat_mul_d2_to_d1(x, trans_x, y, m as usize, k as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_mat_mul_d2_to_d2(
    x: *const CPUBuffer,
    trans_x: bool,
    y: *const CPUBuffer,
    trans_y: bool,
    m: i32,
    n: i32,
    k: i32,
    b: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    linalg::mat_mul_d2_to_d2(
        x, trans_x, y, trans_y, m as usize, n as usize, k as usize, b as usize, result,
    );
}
