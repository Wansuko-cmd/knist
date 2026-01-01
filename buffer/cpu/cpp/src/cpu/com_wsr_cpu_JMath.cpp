#include "com_wsr_cpu_JMath.h"
#include <cmath>
#include <algorithm>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_exp(
        JNIEnv *env, jobject obj, jobject x, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = std::exp(x_ptr[i]);
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_ln(
        JNIEnv *env, jobject obj, jobject x, jfloat e, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = std::log(x_ptr[i] + e);
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_pow(
        JNIEnv *env, jobject obj, jobject x, jint n, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = std::pow(x_ptr[i], n);
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_sqrt(
        JNIEnv *env, jobject obj, jobject x, jfloat e, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    jlong capacity = env->GetDirectBufferCapacity(x);
    jlong size = capacity / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = std::sqrt(x_ptr[i] + e);
    }
}