use jni::JNIEnv;
use jni::objects::{JClass, JFloatArray, ReleaseMode};
use jni::sys::{jfloat, jfloatArray, jint, jlong};
use std::sync::Arc;

use crate::ops;
use crate::resource::buffer::CPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JBuffer_allocate(
    _env: JNIEnv,
    _class: JClass,
    size: jint,
    runtime: jlong,
) -> jlong {
    let runtime = unsafe { &*(runtime as *const Runtime) };
    let buffer = ops::buffer::create(size as usize, runtime);
    Arc::into_raw(Arc::new(buffer)) as jlong
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JBuffer_release(
    _env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    runtime: jlong,
) {
    let buffer = unsafe { Arc::from_raw(ptr as *const CPUBuffer) };
    let runtime = unsafe { &*(runtime as *const Runtime) };
    if let Ok(buffer) = Arc::try_unwrap(buffer) {
        ops::buffer::release(buffer, runtime);
    }
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JBuffer_get(
    _env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    index: jint,
) -> jfloat {
    let buffer = unsafe { &*(ptr as *const CPUBuffer) };
    buffer[index as usize]
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JBuffer_set(
    _env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    index: jint,
    value: jfloat,
) {
    let buffer = unsafe { &mut *(ptr as *mut CPUBuffer) };
    buffer[index as usize] = value;
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JBuffer_readAll(
    env: JNIEnv,
    _class: JClass,
    ptr: jlong,
) -> jfloatArray {
    let buffer = unsafe { &*(ptr as *const CPUBuffer) };
    let result = env
        .new_float_array(buffer.count() as i32)
        .expect("failed to allocate result float[]");
    env.set_float_array_region(&result, 0, buffer)
        .expect("failed to copy into result float[]");
    result.into_raw()
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JBuffer_writeAll(
    mut env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    value: JFloatArray,
) {
    let elements = unsafe {
        env.get_array_elements(&value, ReleaseMode::NoCopyBack)
            .expect("failed to access source float[]")
    };
    let buffer = unsafe { &mut *(ptr as *mut CPUBuffer) };
    buffer.copy_from_slice(&elements);
}
