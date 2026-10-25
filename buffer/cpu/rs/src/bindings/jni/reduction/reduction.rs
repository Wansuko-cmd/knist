use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jfloat, jint, jlong};

use crate::ops::reduction;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_averageD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::average_d1(x);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_averageD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::average_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_averageD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_maxD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::max_d1(x);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_maxD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::max_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_maxD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_minD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::min_d1(x);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_minD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::min_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_minD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_sumD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::sum_d1(x);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_sumD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::sum_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_sumD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_maxIndexD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::max_index_d1(x) as f32;
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_maxIndexD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::max_index_d2(x, xi as usize, xj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_maxIndexD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_topKD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    k: jint,
    seed: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::top_k_d1(x, k as usize, seed as u64) as f32;
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_topKD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    k: jint,
    axis: jint,
    seed: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_topKD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    k: jint,
    axis: jint,
    seed: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
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
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_topPD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    p: jfloat,
    seed: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    result[0] = reduction::top_p_d1(x, p as f32, seed as u64) as f32;
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_topPD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    p: jfloat,
    axis: jint,
    seed: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::top_p_d2(
        x,
        xi as usize,
        xj as usize,
        p as f32,
        axis as usize,
        result,
        seed as u64,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_reduction_JReduction_topPD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    p: jfloat,
    axis: jint,
    seed: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    reduction::top_p_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        p as f32,
        axis as usize,
        result,
        seed as u64,
    );
}
