#include "com_wsr_cpu_JMatMul.h"
#include <mat_mul_fun.h>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMatMul_inner
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint b, jobject result) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *y_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(y));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    inner(x_ptr, y_ptr, size, (int)b, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMatMul_matMulD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jboolean transY, jint n, jint k, jobject result) {
    jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
    jfloat *y_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(y));
    jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    mat_mul_d1_to_d2(x_ptr, y_ptr, (bool)transY, (int)n, (int)k, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMatMul_matMulD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jboolean transX, jobject y, jint m, jint k, jobject result) {
     jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
     jfloat *y_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(y));
     jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
     size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

     mat_mul_d2_to_d1(x_ptr, (bool)transX, y_ptr, (int)m, (int)k, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JMatMul_matMulD2ToD2
  (JNIEnv *env, jobject obj, jobject x, jboolean transX, jobject y, jboolean transY, jint m, jint n, jint k, jint b, jobject result) {
     jfloat *x_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(x));
     jfloat *y_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(y));
     jfloat *result_ptr = static_cast<jfloat*>(env->GetDirectBufferAddress(result));
     size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

     mat_mul_d2_to_d2(x_ptr, (bool)transX, y_ptr, (bool)transY, (int)m, (int)n, (int)k, (int)b, result_ptr);
}
