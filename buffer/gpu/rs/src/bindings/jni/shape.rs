use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jlong};

use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_transposeD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::transpose_d2(x, xi as usize, xj as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_transposeD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis_i: jint,
    axis_j: jint,
    axis_k: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::transpose_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis_i as usize,
        axis_j as usize,
        axis_k as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_transposeD4(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    xl: jint,
    axis_i: jint,
    axis_j: jint,
    axis_k: jint,
    axis_l: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::transpose_d4(
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
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_sliceD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    start: jint,
    end: jint,
    step: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::slice_d1(
        x,
        start as usize,
        end as usize,
        step as isize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_sliceD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    axis: jint,
    start: jint,
    end: jint,
    step: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::slice_d2(
        x,
        xi as usize,
        xj as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_sliceD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    axis: jint,
    start: jint,
    end: jint,
    step: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::slice_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_copyIntoD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    start: jint,
    end: jint,
    step: jint,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::copy_into_d1(
        x,
        result,
        start as usize,
        end as usize,
        step as isize,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_copyIntoD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    ri: jint,
    rj: jint,
    axis: jint,
    start: jint,
    end: jint,
    step: jint,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::copy_into_d2(
        x,
        result,
        ri as usize,
        rj as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_copyIntoD3(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    result: jlong,
    ri: jint,
    rj: jint,
    rk: jint,
    axis: jint,
    start: jint,
    end: jint,
    step: jint,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::copy_into_d3(
        x,
        result,
        ri as usize,
        rj as usize,
        rk as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_flipD3(
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

    ops::shape::flip_d3(
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
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_unfoldD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    b: jint,
    window: jint,
    stride: jint,
    dilation: jint,
    padding: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::unfold_d1(
        x,
        xi as usize,
        xj as usize,
        b as usize,
        window as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_unfoldD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    b: jint,
    window: jint,
    stride: jint,
    dilation: jint,
    padding: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::unfold_d2(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        b as usize,
        window as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_foldD1(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    b: jint,
    stride: jint,
    dilation: jint,
    padding: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::fold_d1(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        b as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_shape_JShape_foldD2(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    xi: jint,
    xj: jint,
    xk: jint,
    xl: jint,
    b: jint,
    stride: jint,
    dilation: jint,
    padding: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::shape::fold_d2(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        b as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}
