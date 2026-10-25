use crate::ops::shape;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_transpose_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::transpose_d2(x, xi as usize, xj as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_transpose_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis_i: i32,
    axis_j: i32,
    axis_k: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::transpose_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis_i as usize,
        axis_j as usize,
        axis_k as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_transpose_d4(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    axis_i: i32,
    axis_j: i32,
    axis_k: i32,
    axis_l: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::transpose_d4(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        axis_i as usize,
        axis_j as usize,
        axis_k as usize,
        axis_l as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_transpose_d5(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    xm: i32,
    axis_i: i32,
    axis_j: i32,
    axis_k: i32,
    axis_l: i32,
    axis_m: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::transpose_d5(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        xm as usize,
        axis_i as usize,
        axis_j as usize,
        axis_k as usize,
        axis_l as usize,
        axis_m as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_slice_d1(
    x: *const CPUBuffer,
    start: i32,
    end: i32,
    step: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::slice_d1(x, start as usize, end as usize, step as isize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_slice_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::slice_d2(
        x,
        xi as usize,
        xj as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_slice_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::slice_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_copy_into_d1(
    x: *const CPUBuffer,
    result: *mut CPUBuffer,
    start: i32,
    end: i32,
    step: i32,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::copy_into_d1(x, result, start as usize, end as usize, step as isize);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_copy_into_d2(
    x: *const CPUBuffer,
    result: *mut CPUBuffer,
    ri: i32,
    rj: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::copy_into_d2(
        x,
        result,
        ri as usize,
        rj as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_copy_into_d3(
    x: *const CPUBuffer,
    result: *mut CPUBuffer,
    ri: i32,
    rj: i32,
    rk: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::copy_into_d3(
        x,
        result,
        ri as usize,
        rj as usize,
        rk as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_unfold_d1(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    b: i32,
    window: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::unfold_d1(
        x,
        result,
        xi as usize,
        xj as usize,
        b as usize,
        window as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_unfold_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    b: i32,
    window: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::unfold_d2(
        x,
        result,
        xi as usize,
        xj as usize,
        xk as usize,
        b as usize,
        window as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_fold_d1(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    b: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result.fill(0.0);
    shape::fold_d1(
        x,
        result,
        xi as usize,
        xj as usize,
        xk as usize,
        b as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_fold_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    b: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result.fill(0.0);
    shape::fold_d2(
        x,
        result,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        b as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_flip_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    shape::flip_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
    );
}
