use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::jlong;

use crate::ops::runtime;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JRuntime_allocate(
    _: JNIEnv,
    _class: JClass,
    pool_size: jlong,
) -> jlong {
    let runtime = runtime::allocate(pool_size as u64);
    Box::into_raw(Box::new(runtime)) as jlong
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_JRuntime_release(
    _: JNIEnv,
    _class: JClass,
    ptr: jlong,
) {
    let _ = unsafe { Box::from_raw(ptr as *mut Runtime) };
}
