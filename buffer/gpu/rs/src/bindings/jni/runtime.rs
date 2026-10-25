use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jboolean, jlong};

use crate::ops::runtime;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JRuntime_allocate(
    _: JNIEnv,
    _class: JClass,
    pool_size: jlong,
    enable_profiler: jboolean,
) -> jlong {
    let runtime = runtime::allocate(pool_size as u64, enable_profiler != 0);
    Box::into_raw(Box::new(runtime)) as jlong
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JRuntime_release(
    _: JNIEnv,
    _class: JClass,
    ptr: jlong,
) {
    let _ = unsafe { Box::from_raw(ptr as *mut Runtime) };
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JRuntime_flush(
    _: JNIEnv,
    _class: JClass,
    runtime_ptr: jlong,
) {
    let runtime = unsafe { &*(runtime_ptr as *const Runtime) };
    runtime.flush();
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_JRuntime_sync(
    _: JNIEnv,
    _class: JClass,
    runtime_ptr: jlong,
) {
    let runtime = unsafe { &*(runtime_ptr as *const Runtime) };
    runtime.flush();
    runtime.wait();
}
