use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jfloat, jint, jlong};

use crate::ops::elementwise::operation::times;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD0ToD1(
    _env: JNIEnv,
    _class: JClass,
    x: jfloat,
    y: jlong,
    result: jlong,
) {
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d0_to_d1(x as f32, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD1ToD0(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jfloat,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d1_to_d0(x, y as f32, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD1ToD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d1_to_d1(x, y, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD1ToD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    yi: jint,
    yj: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d1_to_d2(x, y, yi as usize, yj as usize, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD1ToD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    yi: jint,
    yj: jint,
    yk: jint,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d1_to_d3(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD2ToD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    y: jlong,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d2_to_d1(x, xi as usize, xj as usize, y, axis as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD2ToD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    y: jlong,
    yi: jint,
    yj: jint,
    yk: jint,
    axis1: jint,
    axis2: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d2_to_d3(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD3ToD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    y: jlong,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d3_to_d1(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD3ToD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    y: jlong,
    yi: jint,
    yj: jint,
    axis1: jint,
    axis2: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d3_to_d2(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD3ToD4(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    y: jlong,
    yi: jint,
    yj: jint,
    yk: jint,
    yl: jint,
    axis1: jint,
    axis2: jint,
    axis3: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d3_to_d4(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD4ToD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    xl: jint,
    y: jlong,
    axis: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d4_to_d1(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD4ToD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    xl: jint,
    y: jlong,
    yi: jint,
    yj: jint,
    axis1: jint,
    axis2: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d4_to_d2(
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
pub extern "system" fn Java_com_wsr_knist_cpu_elementwise_operation_times_JTimes_timesD4ToD3(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    xl: jint,
    y: jlong,
    yi: jint,
    yj: jint,
    yk: jint,
    axis1: jint,
    axis2: jint,
    axis3: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    times::times_with_d4_to_d3(
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
