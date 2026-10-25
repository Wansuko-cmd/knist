use std::sync::Arc;

use jni::JNIEnv;
use jni::objects::{JClass, JFloatArray, ReleaseMode};
use jni::sys::{jfloat, jfloatArray, jint, jlong};

use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JBuffer_allocate(
    _: JNIEnv,
    _class: JClass,
    size: jint,
    runtime: jlong,
) -> jlong {
    let runtime = unsafe { &*(runtime as *const Runtime) };
    let buffer = ops::buffer::create(size as usize, runtime);
    Arc::into_raw(Arc::new(buffer)) as jlong
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JBuffer_init(
    mut env: JNIEnv,
    _class: JClass,
    value: JFloatArray,
    runtime: jlong,
) -> jlong {
    let value = unsafe {
        env.get_array_elements(&value, ReleaseMode::NoCopyBack)
            .unwrap()
    };
    let runtime = unsafe { &*(runtime as *const Runtime) };
    let buffer = ops::buffer::init(&value, runtime);
    Arc::into_raw(Arc::new(buffer)) as jlong
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JBuffer_release(
    _: JNIEnv,
    _class: JClass,
    ptr: jlong,
    runtime: jlong,
) {
    let buffer = unsafe { Arc::from_raw(ptr as *const GPUBuffer) };
    let runtime = unsafe { &*(runtime as *const Runtime) };
    if let Ok(buffer) = Arc::try_unwrap(buffer) {
        ops::buffer::release(buffer, runtime);
    }
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JBuffer_readAll(
    env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    runtime: jlong,
) -> jfloatArray {
    let buffer = unsafe { &*(ptr as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    let buffer = ops::buffer::read_all(buffer, runtime);

    let result = env.new_float_array(buffer.len() as i32).unwrap();
    let _ = env.set_float_array_region(&result, 0, &buffer);
    result.into_raw()
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JBuffer_write(
    _: JNIEnv,
    _class: JClass,
    ptr: jlong,
    index: jint,
    value: jfloat,
    runtime: jlong,
) {
    let buffer = unsafe { &*(ptr as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };
    ops::buffer::write(buffer, index as usize, value, runtime);
}
