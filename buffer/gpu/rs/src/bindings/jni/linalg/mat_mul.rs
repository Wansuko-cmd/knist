use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jboolean, jint, jlong};

use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_gpu_linalg_JMatMul_matMul(
    _: JNIEnv,
    _class: JClass,
    x: jlong,
    trans_x: jboolean,
    y: jlong,
    trans_y: jboolean,
    m: jint,
    n: jint,
    k: jint,
    b: jint,
    result: jlong,
    runtime: jlong,
) {
    let x = unsafe { &*(x as *const GPUBuffer) };
    let y = unsafe { &*(y as *const GPUBuffer) };
    let result = unsafe { &*(result as *const GPUBuffer) };
    let runtime = unsafe { &mut *(runtime as *mut Runtime) };

    ops::linalg::mat_mul::mat_mul(
        x,
        trans_x != 0,
        y,
        trans_y != 0,
        m as usize,
        n as usize,
        k as usize,
        b as usize,
        result,
        runtime,
    );
}
