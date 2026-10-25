use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jboolean, jint, jlong};

use crate::ops::linalg;
use crate::resource::buffer::CPUBuffer;

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_linalg_JMatMul_inner(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    b: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    linalg::inner(x, y, b as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_linalg_JMatMul_matMulD1ToD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    y: jlong,
    trans_y: jboolean,
    n: jint,
    k: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    linalg::mat_mul_d1_to_d2(x, y, trans_y != 0, n as usize, k as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_linalg_JMatMul_matMulD2ToD1(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    trans_x: jboolean,
    y: jlong,
    m: jint,
    k: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    linalg::mat_mul_d2_to_d1(x, trans_x != 0, y, m as usize, k as usize, result);
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_wsr_knist_cpu_linalg_JMatMul_matMulD2ToD2(
    _env: JNIEnv,
    _class: JClass,
    x: jlong,
    trans_x: jboolean,
    y: jlong,
    trans_y: jboolean,
    m: jint,
    k: jint,
    n: jint,
    b: jint,
    result: jlong,
) {
    let x = unsafe { &*(x as *const CPUBuffer) };
    let y = unsafe { &*(y as *const CPUBuffer) };
    let result = unsafe { &mut *(result as *mut CPUBuffer) };
    linalg::mat_mul_d2_to_d2(
        x,
        trans_x != 0,
        y,
        trans_y != 0,
        m as usize,
        k as usize,
        n as usize,
        b as usize,
        result,
    );
}
