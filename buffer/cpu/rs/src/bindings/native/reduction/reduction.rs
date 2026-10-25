use crate::ops::reduction;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_average_d1(x: *const CPUBuffer, result: *mut CPUBuffer) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::average_d1(x);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_average_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::average_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_average_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::average_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_max_d1(x: *const CPUBuffer, result: *mut CPUBuffer) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::max_d1(x);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_max_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::max_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_max_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::max_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_min_d1(x: *const CPUBuffer, result: *mut CPUBuffer) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::min_d1(x);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_min_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::min_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_min_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::min_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_sum_d1(x: *const CPUBuffer, result: *mut CPUBuffer) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::sum_d1(x);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_sum_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::sum_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_sum_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::sum_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_max_index_d1(x: *const CPUBuffer, result: *mut CPUBuffer) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::max_index_d1(x) as f32;
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_max_index_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::max_index_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_max_index_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::max_index_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_top_k_d1(
    x: *const CPUBuffer,
    k: i32,
    seed: i64,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::top_k_d1(x, k as usize, seed as u64) as f32;
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_top_k_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    k: i32,
    axis: i32,
    seed: i64,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::top_k_d2(
        x,
        xi as usize,
        xj as usize,
        k as usize,
        axis as usize,
        result,
        seed as u64,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_top_k_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    k: i32,
    axis: i32,
    seed: i64,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::top_k_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        k as usize,
        axis as usize,
        result,
        seed as u64,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_top_p_d1(
    x: *const CPUBuffer,
    p: f32,
    seed: i64,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    result[0] = reduction::top_p_d1(x, p, seed as u64) as f32;
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_top_p_d2(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    p: f32,
    axis: i32,
    seed: i64,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::top_p_d2(
        x,
        xi as usize,
        xj as usize,
        p,
        axis as usize,
        result,
        seed as u64,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_top_p_d3(
    x: *const CPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    p: f32,
    axis: i32,
    seed: i64,
    result: *mut CPUBuffer,
) {
    let x = unsafe { &*x };
    let result = unsafe { &mut *result };
    reduction::top_p_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        p,
        axis as usize,
        result,
        seed as u64,
    );
}
