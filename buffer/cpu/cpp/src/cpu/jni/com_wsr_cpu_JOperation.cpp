#include "com_wsr_cpu_JOperation.h"
#include <operation_fun.h>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD0ToD1
(JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t y_size = static_cast<size_t>(env->GetDirectBufferCapacity(y)) / sizeof(jfloat);

    plus_d0_to_d1(x, y_ptr, y_size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD0
(JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t x_size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    plus_d1_to_d0(x_ptr, x_size, y, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD1
(JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(result)) / sizeof(jfloat);

    plus_d1_to_d1(x_ptr, y_ptr, size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD2
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d1_to_d2(x_ptr, y_ptr, yi, yj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD3
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d1_to_d3(x_ptr, y_ptr, yi, yj, yk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD2ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d2_to_d1(x_ptr, xi, xj, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD2ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d2_to_d3(x_ptr, xi, xj, y_ptr, yi, yj, yk, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d3_to_d1(x_ptr, xi, xj, xk, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d3_to_d2(x_ptr, xi, xj, xk, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d3_to_d4(x_ptr, xi, xj, xk, y_ptr, yi, yj, yk, yl, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
     jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
     jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
     jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

     plus_d4_to_d1(x_ptr, xi, xj, xk, xl, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d4_to_d2(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    plus_d4_to_d3(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, yk, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD0ToD1
(JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t y_size = static_cast<size_t>(env->GetDirectBufferCapacity(y)) / sizeof(jfloat);

    minus_d0_to_d1(x, y_ptr, y_size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD0
(JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t x_size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    minus_d1_to_d0(x_ptr, x_size, y, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD1
(JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(result)) / sizeof(jfloat);

    minus_d1_to_d1(x_ptr, y_ptr, size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD2
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d1_to_d2(x_ptr, y_ptr, yi, yj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD3
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d1_to_d3(x_ptr, y_ptr, yi, yj, yk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD2ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d2_to_d1(x_ptr, xi, xj, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD2ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d2_to_d3(x_ptr, xi, xj, y_ptr, yi, yj, yk, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d3_to_d1(x_ptr, xi, xj, xk, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d3_to_d2(x_ptr, xi, xj, xk, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d3_to_d4(x_ptr, xi, xj, xk, y_ptr, yi, yj, yk, yl, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
     jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
     jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
     jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

     minus_d4_to_d1(x_ptr, xi, xj, xk, xl, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d4_to_d2(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    minus_d4_to_d3(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, yk, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD0ToD1
(JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t y_size = static_cast<size_t>(env->GetDirectBufferCapacity(y)) / sizeof(jfloat);

    times_d0_to_d1(x, y_ptr, y_size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD0
(JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t x_size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    times_d1_to_d0(x_ptr, x_size, y, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD1
(JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(result)) / sizeof(jfloat);

    times_d1_to_d1(x_ptr, y_ptr, size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD2
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d1_to_d2(x_ptr, y_ptr, yi, yj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD3
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d1_to_d3(x_ptr, y_ptr, yi, yj, yk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD2ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d2_to_d1(x_ptr, xi, xj, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD2ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d2_to_d3(x_ptr, xi, xj, y_ptr, yi, yj, yk, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d3_to_d1(x_ptr, xi, xj, xk, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d3_to_d2(x_ptr, xi, xj, xk, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d3_to_d4(x_ptr, xi, xj, xk, y_ptr, yi, yj, yk, yl, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
     jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
     jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
     jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

     times_d4_to_d1(x_ptr, xi, xj, xk, xl, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d4_to_d2(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    times_d4_to_d3(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, yk, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD0ToD1
(JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t y_size = static_cast<size_t>(env->GetDirectBufferCapacity(y)) / sizeof(jfloat);

    div_d0_to_d1(x, y_ptr, y_size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD0
(JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t x_size = static_cast<size_t>(env->GetDirectBufferCapacity(x)) / sizeof(jfloat);

    div_d1_to_d0(x_ptr, x_size, y, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD1
(JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    size_t size = static_cast<size_t>(env->GetDirectBufferCapacity(result)) / sizeof(jfloat);

    div_d1_to_d1(x_ptr, y_ptr, size, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD2
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d1_to_d2(x_ptr, y_ptr, yi, yj, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD3
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d1_to_d3(x_ptr, y_ptr, yi, yj, yk, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD2ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d2_to_d1(x_ptr, xi, xj, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD2ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d2_to_d3(x_ptr, xi, xj, y_ptr, yi, yj, yk, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d3_to_d1(x_ptr, xi, xj, xk, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d3_to_d2(x_ptr, xi, xj, xk, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d3_to_d4(x_ptr, xi, xj, xk, y_ptr, yi, yj, yk, yl, axis1, axis2, axis3, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
     jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
     jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
     jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

     div_d4_to_d1(x_ptr, xi, xj, xk, xl, y_ptr, axis, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d4_to_d2(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, axis1, axis2, result_ptr);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    div_d4_to_d3(x_ptr, xi, xj, xk, xl, y_ptr, yi, yj, yk, axis1, axis2, axis3, result_ptr);
}