use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jlong};

use crate::ops::index;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_index_JIndex_gather(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    i: jint,
    j: jint,
    k: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };

    index::gather(x, y, i as usize, j as usize, k as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_index_JIndex_scatterAdd(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    i: jint,
    j: jint,
    k: jint,
    b: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };

    index::scatter_add(x, y, i as usize, j as usize, k as usize, b as usize, result);
}
