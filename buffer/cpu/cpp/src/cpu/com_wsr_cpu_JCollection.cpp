#include "com_wsr_cpu_JCollection.h"
#include <stdio.h>
#include <cblas.h>

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_max(
        JNIEnv *env, jobject, jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    float acc = x_ptr[0];
    for (int i = 1; i < size; i++) {
        if (acc < x_ptr[i]) {
            acc = x_ptr[i];
        }
    }
    return (jfloat)acc;
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD1(
        JNIEnv *env, jobject obj, jobject x, jint xb, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    int stride = size / xb;
    for (int b = 0; b < xb; b++) {
        int offset = b * stride;
        float acc = x_ptr[offset];
        for (int i = 1; i < stride; i++) {
            if (acc < x_ptr[offset + i]) {
                acc = x_ptr[offset + i];
            }
        }
        result_ptr[b] = acc;
    }
}


JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            float acc = x_ptr[j];
            for (int i = 1; i < xi; i++) {
                if (acc < x_ptr[i * xj + j]) {
                    acc = x_ptr[i * xj + j];
                }
            }
            result_ptr[j] = acc;
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            float acc = x_ptr[i * xj];
            for (int j = 1; j < xj; j++) {
                if (acc < x_ptr[i * xj + j]) {
                    acc = x_ptr[i * xj + j];
                }
            }
            result_ptr[i] = acc;
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                float acc = x_ptr[j * xk + k];
                for (int i = 1; i < xi; i++) {
                    if (acc < x_ptr[(i * xj + j) * xk + k]) {
                        acc = x_ptr[(i * xj + j) * xk + k];
                    }
                }
                result_ptr[j * xk + k] = acc;
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                float acc = x_ptr[i * xj * xk + k];
                for (int j = 1; j < xj; j++) {
                    if (acc < x_ptr[(i * xj + j) * xk + k]) {
                        acc = x_ptr[(i * xj + j) * xk + k];
                    }
                }
                result_ptr[i * xk + k] = acc;
            }
        }
    } else if (axis == 2) {
        // axis 2 - 各i,j組み合わせについて最大値を求める
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float acc = x_ptr[(i * xj + j) * xk];
                for (int k = 1; k < xk; k++) {
                    if (acc < x_ptr[(i * xj + j) * xk + k]) {
                        acc = x_ptr[(i * xj + j) * xk + k];
                    }
                }
                result_ptr[i * xj + j] = acc;
            }
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_maxD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                float acc = x_ptr[(j * xk + k) * xl + l];
                    for (int i = 1; i < xi; i++) {
                        if (acc < x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
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
                        if (acc < x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
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
                        if (acc < x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
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
                        if (acc < x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
                    }
                    result_ptr[(i * xj + j) * xk + k] = acc;
                }
            }
        }
    }
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_min(
        JNIEnv *env, jobject obj, jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    float acc = x_ptr[0];
    for (int i = 1; i < size; i++) {
        if (acc > x_ptr[i]) {
            acc = x_ptr[i];
        }
    }
    return (jfloat)acc;
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD1(
        JNIEnv *env, jobject obj, jobject x, jint xb, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    int stride = size / xb;
    for (int b = 0; b < xb; b++) {
        int offset = b * stride;
        float acc = x_ptr[offset];
        for (int i = 1; i < stride; i++) {
            if (acc > x_ptr[offset + i]) {
                acc = x_ptr[offset + i];
            }
        }
        result_ptr[b] = acc;
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            float acc = x_ptr[j];
            for (int i = 1; i < xi; i++) {
                if (acc > x_ptr[i * xj + j]) {
                    acc = x_ptr[i * xj + j];
                }
            }
            result_ptr[j] = acc;
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            float acc = x_ptr[i * xj];
            for (int j = 1; j < xj; j++) {
                if (acc > x_ptr[i * xj + j]) {
                    acc = x_ptr[i * xj + j];
                }
            }
            result_ptr[i] = acc;
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                float acc = x_ptr[j * xk + k];
                for (int i = 1; i < xi; i++) {
                    if (acc > x_ptr[(i * xj + j) * xk + k]) {
                        acc = x_ptr[(i * xj + j) * xk + k];
                    }
                }
                result_ptr[j * xk + k] = acc;
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                float acc = x_ptr[i * xj * xk + k];
                for (int j = 1; j < xj; j++) {
                    if (acc > x_ptr[(i * xj + j) * xk + k]) {
                    acc = x_ptr[(i * xj + j) * xk + k];
                    }
                }
                result_ptr[i * xk + k] = acc;
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float acc = x_ptr[(i * xj + j) * xk];
                for (int k = 1; k < xk; k++) {
                    if (acc > x_ptr[(i * xj + j) * xk + k]) {
                        acc = x_ptr[(i * xj + j) * xk + k];
                    }
                }
                result_ptr[i * xj + j] = acc;
            }
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_minD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x_ptr[(j * xk + k) * xl + l];
                    for (int i = 1; i < xi; i++) {
                        if (acc > x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
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
                    if (acc > x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                        acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                    }
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
                        if (acc > x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
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
                        if (acc > x_ptr[((i * xj + j) * xk + k) * xl + l]) {
                            acc = x_ptr[((i * xj + j) * xk + k) * xl + l];
                        }
                    }
                    result_ptr[(i * xj + j) * xk + k] = acc;
                }
            }
        }
    }
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cpu_JCollection_sum(
        JNIEnv *env, jobject obj, jobject x
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    float acc = 0.0f;
    for (int i = 0; i < size; i++) {
        acc += x_ptr[i];
    }
    return (jfloat)acc;
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD1(
        JNIEnv *env, jobject obj, jobject x, jint xb, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    int stride = size / xb;
    for (int b = 0; b < xb; b++) {
        int offset = b * stride;
        float acc = 0.0f;
        for (int i = 0; i < stride; i++) {
            acc += x_ptr[offset + i];
        }
        result_ptr[b] = acc;
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD2(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            float acc = 0.0f;
            for (int i = 0; i < xi; i++) {
                acc += x_ptr[i * xj + j];
            }
            result_ptr[j] = acc;
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            float acc = 0.0f;
            for (int j = 0; j < xj; j++) {
                acc += x_ptr[i * xj + j];
            }
            result_ptr[i] = acc;
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD3(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                float acc = 0.0f;
                for (int i = 0; i < xi; i++) {
                    acc += x_ptr[(i * xj + j) * xk + k];
                }
                result_ptr[j * xk + k] = acc;
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                float acc = 0.0f;
                for (int j = 0; j < xj; j++) {
                    acc += x_ptr[(i * xj + j) * xk + k];
                }
                result_ptr[i * xk + k] = acc;
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float acc = 0.0f;
                for (int k = 0; k < xk; k++) {
                    acc += x_ptr[(i * xj + j) * xk + k];
                }
                result_ptr[i * xj + j] = acc;
            }
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JCollection_sumD4(
        JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jint axis, jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = 0.0f;
                    for (int i = 0; i < xi; i++) {
                        acc += x_ptr[((i * xj + j) * xk + k) * xl + l];
                    }
                    result_ptr[(j * xk + k) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = 0.0f;
                    for (int j = 0; j < xj; j++) {
                        acc += x_ptr[((i * xj + j) * xk + k) * xl + l];
                    }
                    result_ptr[(i * xk + k) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int l = 0; l < xl; l++) {
                    float acc = 0.0f;
                    for (int k = 0; k < xk; k++) {
                        acc += x_ptr[((i * xj + j) * xk + k) * xl + l];
                    }
                    result_ptr[(i * xj + j) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float acc = 0.0f;
                    for (int l = 0; l < xl; l++) {
                        acc += x_ptr[((i * xj + j) * xk + k) * xl + l];
                    }
                    result_ptr[(i * xj + j) * xk + k] = acc;
                }
            }
        }
    }
}