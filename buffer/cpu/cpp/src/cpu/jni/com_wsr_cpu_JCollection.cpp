#include "com_wsr_cpu_JCollection.h"
#include <stdio.h>
#include <cblas.h>

#define PERFORM_OPERATION(acc, x) \
    if constexpr (Op == Operation::Max) { \
        if (acc < x) { \
            acc = x; \
        } \
    } else if constexpr (Op == Operation::Min) { \
        if (acc > x) { \
            acc = x; \
        } \
    } else if constexpr (Op == Operation::Sum) { \
        acc += x; \
    }

enum class Operation {
    Max,
    Min,
    Sum
};

template<Operation Op>
inline float reduceD1(
    JNIEnv *env, jobject obj,
    jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    float acc = x_ptr[0];
    for (int i = 1; i < size; i++) {
        PERFORM_OPERATION(acc, x_ptr[i]);
    }
    return acc;
}

template<Operation Op>
inline void reduceD2(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            float acc = x_ptr[j];
            for (int i = 1; i < xi; i++) {
                PERFORM_OPERATION(acc, x_ptr[i * xj + j]);
            }
            result_ptr[j] = acc;
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            float acc = x_ptr[i * xj];
            for (int j = 1; j < xj; j++) {
                PERFORM_OPERATION(acc, x_ptr[i * xj + j]);
            }
            result_ptr[i] = acc;
        }
    }
}

template<Operation Op>
inline void reduceD3(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                float acc = x_ptr[j * xk + k];
                for (int i = 1; i < xi; i++) {
                    PERFORM_OPERATION(acc, x_ptr[(i * xj + j) * xk + k]);
                }
                result_ptr[j * xk + k] = acc;
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                float acc = x_ptr[i * xj * xk + k];
                for (int j = 1; j < xj; j++) {
                    PERFORM_OPERATION(acc, x_ptr[(i * xj + j) * xk + k]);
                }
                result_ptr[i * xk + k] = acc;
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float acc = x_ptr[(i * xj + j) * xk];
                for (int k = 1; k < xk; k++) {
                    PERFORM_OPERATION(acc, x_ptr[(i * xj + j) * xk + k]);
                }
                result_ptr[i * xj + j] = acc;
            }
        }
    }
}

template<Operation Op>
inline void reduceD4(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk, jint xl,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x_ptr[(j * xk + k) * xl + l];
                    for (int i = 1; i < xi; i++) {
                        PERFORM_OPERATION(acc, x_ptr[((i * xj + j) * xk + k) * xl + l]);
                    }
                    result_ptr[(j * xk + k) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x_ptr[(i * xj * xk + k) * xl + l];
                    for (int j = 1; j < xj; j++) {
                        PERFORM_OPERATION(acc, x_ptr[((i * xj + j) * xk + k) * xl + l]);
                    }
                    result_ptr[(i * xk + k) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x_ptr[(i * xj + j) * xk * xl + l];
                    for (int k = 1; k < xk; k++) {
                        PERFORM_OPERATION(acc, x_ptr[((i * xj + j) * xk + k) * xl + l]);
                    }
                    result_ptr[(i * xj + j) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float acc = x_ptr[((i * xj + j) * xk + k) * xl];
                    for (int l = 1; l < xl; l++) {
                        PERFORM_OPERATION(acc, x_ptr[((i * xj + j) * xk + k) * xl + l]);
                    }
                    result_ptr[(i * xj + j) * xk + k] = acc;
                }
            }
        }
    }
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_maxD1(
        JNIEnv *env, jobject obj, jobject x
) {
    return (jfloat)reduceD1<Operation::Max>(env, obj, x);
}


JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    reduceD2<Operation::Max>(env, obj, x, xi, xj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    reduceD3<Operation::Max>(env, obj, x, xi, xj, xk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    reduceD4<Operation::Max>(env, obj, x, xi, xj, xk, xl, axis, result);
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_minD1(
        JNIEnv *env, jobject obj, jobject x
) {
    return (jfloat)reduceD1<Operation::Min>(env, obj, x);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    reduceD2<Operation::Min>(env, obj, x, xi, xj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    reduceD3<Operation::Min>(env, obj, x, xi, xj, xk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    reduceD4<Operation::Min>(env, obj, x, xi, xj, xk, xl, axis, result);
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_sumD1(
        JNIEnv *env, jobject obj, jobject x
) {
    return (jfloat)reduceD1<Operation::Sum>(env, obj, x);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    reduceD2<Operation::Sum>(env, obj, x, xi, xj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    reduceD3<Operation::Sum>(env, obj, x, xi, xj, xk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    reduceD4<Operation::Sum>(env, obj, x, xi, xj, xk, xl, axis, result);
}