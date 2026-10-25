use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jlong};

use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_averageD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::average_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_averageD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::average_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_averageD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::average_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_maxD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::max_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_maxD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::max_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_maxD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::max_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_minD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::min_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_minD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::min_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_minD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::min_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_sumD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::sum_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_sumD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::sum_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_reduction_JReduction_sumD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::reduction::sum_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}
