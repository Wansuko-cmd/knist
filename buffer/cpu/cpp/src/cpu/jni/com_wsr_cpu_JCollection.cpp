#include "com_wsr_cpu_JCollection.h"
#include <collection_fun.h>

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_maxD1(
        JNIEnv *env, jobject obj, jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);
    return (jfloat)max_d1(x_ptr, size);
}


JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    max_d2(x_ptr, xi, xj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    max_d3(x_ptr, xi, xj, xk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    max_d4(x_ptr, xi, xj, xk, xl, axis, result_ptr);
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_minD1(
        JNIEnv *env, jobject obj, jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);
    return (jfloat)min_d1(x_ptr, size);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    min_d2(x_ptr, xi, xj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    min_d3(x_ptr, xi, xj, xk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    min_d4(x_ptr, xi, xj, xk, xl, axis, result_ptr);
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_sumD1(
        JNIEnv *env, jobject obj, jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);
    return (jfloat)sum_d1(x_ptr, size);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    sum_d2(x_ptr, xi, xj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    sum_d3(x_ptr, xi, xj, xk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    sum_d4(x_ptr, xi, xj, xk, xl, axis, result_ptr);
}