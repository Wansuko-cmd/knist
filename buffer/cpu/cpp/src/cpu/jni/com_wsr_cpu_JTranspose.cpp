#include "com_wsr_cpu_JTranspose.h"
#include <transpose_fun.h>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD2(
        JNIEnv *env, jobject, jobject x, jint xi, jint xj, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    transpose_d2(x_ptr, xi, xj, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD3(
        JNIEnv *env, jobject, jobject x, jint xi, jint xj, jint xk,
        jint axis_i, jint axis_j, jint axis_k, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    transpose_d3(x_ptr, xi, xj, xk, axis_i, axis_j, axis_k, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD4(
        JNIEnv *env, jobject, jobject x, jint xi, jint xj, jint xk, jint xl,
        jint axis_i, jint axis_j, jint axis_k, jint axis_l, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    transpose_d4(x_ptr, xi, xj, xk, xl, axis_i, axis_j, axis_k, axis_l, result_ptr);
}
