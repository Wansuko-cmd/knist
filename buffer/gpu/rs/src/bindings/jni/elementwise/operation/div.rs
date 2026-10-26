use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jfloat, jint, jlong};

use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD0ToD1(
    _: JNIEnv,
    _class: JClass,
    x: jfloat,
    y: jlong,
    result: jlong,
    runtime: jlong,
) {
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d0_to_d1(x as f32, &y, &result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD1ToD0(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jfloat,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d1_to_d0(&x, y as f32, &result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD1ToD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d1_to_d1(&x, &y, &result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD1ToD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    yi: jint,
    yj: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d1_to_d2(
        &x,
        &y,
        yi as usize,
        yj as usize,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD1ToD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    yi: jint,
    yj: jint,
    yk: jint,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d1_to_d3(
        &x,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD2ToD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    y: jlong,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d2_to_d1(
        &x,
        xi as usize,
        xj as usize,
        &y,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD2ToD3(
    _: JNIEnv,
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
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d2_to_d3(
        &x,
        xi as usize,
        xj as usize,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis1 as usize,
        axis2 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD3ToD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    y: jlong,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d3_to_d1(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        &y,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD3ToD2(
    _: JNIEnv,
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
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d3_to_d2(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        &y,
        yi as usize,
        yj as usize,
        axis1 as usize,
        axis2 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD3ToD4(
    _: JNIEnv,
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
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d3_to_d4(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        yl as usize,
        axis1 as usize,
        axis2 as usize,
        axis3 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD4ToD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    xl: jint,
    y: jlong,
    axis: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d4_to_d1(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        &y,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD4ToD2(
    _: JNIEnv,
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
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d4_to_d2(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        &y,
        yi as usize,
        yj as usize,
        axis1 as usize,
        axis2 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_elementwise_operation_JDiv_divD4ToD3(
    _: JNIEnv,
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
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::elementwise::operation::div_with_d4_to_d3(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis1 as usize,
        axis2 as usize,
        axis3 as usize,
        &result,
        runtime,
    );
}
