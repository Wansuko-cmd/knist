#include "com_wsr_cpu_JMath.h"
#include <math_fun.h>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_exp(
        JNIEnv *env, jobject obj, jobject x, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    exp_d1(x_ptr, result_ptr, size);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_ln(
        JNIEnv *env, jobject obj, jobject x, jfloat e, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    ln_d1(x_ptr, e, result_ptr, size);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_pow(
        JNIEnv *env, jobject obj, jobject x, jint n, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    pow_d1(x_ptr, n, result_ptr, size);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMath_sqrt(
        JNIEnv *env, jobject obj, jobject x, jfloat e, jobject result
) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    sqrt_d1(x_ptr, e, result_ptr, size);
}
