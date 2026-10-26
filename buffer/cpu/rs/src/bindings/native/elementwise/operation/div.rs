use crate::ops::elementwise::operation::div;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d0_to_d1(x: f32, y: *const CPUBuffer, result: *mut CPUBuffer) {
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d0_to_d1(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d1_to_d0(x: *const CPUBuffer, y: f32, result: *mut CPUBuffer) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    div::div_with_d1_to_d0(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d1_to_d1(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d1_to_d1(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d1_to_d2(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d1_to_d2(x, y, yi as usize, yj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d1_to_d3(
    x: *const CPUBuffer,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d1_to_d3(
        x,
        y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d2_to_d1(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    y: *const CPUBuffer,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d2_to_d1(x, xi as usize, xj as usize, y, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d2_to_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    axis1: i32,
    axis2: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d2_to_d3(
        x,
        xi as usize,
        xj as usize,
        y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis1 as usize,
        axis2 as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d3_to_d1(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    y: *const CPUBuffer,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d3_to_d1(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        y,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d3_to_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    axis1: i32,
    axis2: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d3_to_d2(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        y,
        yi as usize,
        yj as usize,
        axis1 as usize,
        axis2 as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d3_to_d4(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    yl: i32,
    axis1: i32,
    axis2: i32,
    axis3: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d3_to_d4(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        y,
        yi as usize,
        yj as usize,
        yk as usize,
        yl as usize,
        axis1 as usize,
        axis2 as usize,
        axis3 as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d4_to_d1(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    y: *const CPUBuffer,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d4_to_d1(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        y,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d4_to_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    axis1: i32,
    axis2: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d4_to_d2(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        y,
        yi as usize,
        yj as usize,
        axis1 as usize,
        axis2 as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_div_d4_to_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    y: *const CPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    axis1: i32,
    axis2: i32,
    axis3: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &mut *result };
    div::div_with_d4_to_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis1 as usize,
        axis2 as usize,
        axis3 as usize,
        result,
    );
}
