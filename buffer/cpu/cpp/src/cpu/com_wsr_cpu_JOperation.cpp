#include "com_wsr_cpu_JOperation.h"
#include <stdio.h>
#include <cmath>

#define PERFORM_OPERATION(result, x, y) \
    if constexpr (Op == Operation::Plus) { \
        result = x + y; \
    } else if constexpr (Op == Operation::Minus) { \
        result = x - y; \
    } else if constexpr (Op == Operation::Times) { \
        result = x * y; \
    } else if constexpr (Op == Operation::Div) { \
        result = x / y; \
    }

enum class Operation {
    Plus,
    Minus,
    Times,
    Div
};

template<Operation Op>
inline void zipWithD0ToD1(
    JNIEnv *env, jobject obj,
    jfloat x,
    jobject y,
    jobject result
) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(y) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        PERFORM_OPERATION(result_ptr[i], x, y_ptr[i]);
    }
}

template<Operation Op>
inline void zipWithD1ToD0(
    JNIEnv *env, jobject obj,
    jobject x,
    jfloat y,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        PERFORM_OPERATION(result_ptr[i], x_ptr[i], y);
    }
}

template<Operation Op>
inline void zipWithD1ToD1(
    JNIEnv *env, jobject obj,
    jobject x,
    jobject y,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    jlong size = env->GetDirectBufferCapacity(result) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        PERFORM_OPERATION(result_ptr[i], x_ptr[i], y_ptr[i]);
    }
}

template<Operation Op>
inline void zipWithD1ToD2(
    JNIEnv *env, jobject obj,
    jobject x,
    jobject y, jint yi, jint yj,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            int y_i = i * yj;
            for (int j = 0; j < yj; j++) {
                int index = y_i + j;
                PERFORM_OPERATION(result_ptr[index], x_value, y_ptr[index]);
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < yi; i++) {
            int y_i = i * yj;
            for (int j = 0; j < yj; j++) {
                int index = y_i + j;
                PERFORM_OPERATION(result_ptr[index], x_ptr[j], y_ptr[index]);
            }
        }
    }
}

template<Operation Op>
inline void zipWithD1ToD3(
    JNIEnv *env, jobject obj,
    jobject x,
    jobject y, jint yi, jint yj, jint yk,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    jlong x_size = env->GetDirectBufferCapacity(result) / sizeof(jfloat);

    if (axis == 0) {
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            for (int j = 0; j < yj; j++) {
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int index = y_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_value, y_ptr[index]);
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[j];
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int index = y_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_value, y_ptr[index]);
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int index = y_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[k], y_ptr[index]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD2ToD1(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj,
    jobject y,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            int x_i = i * xj;
            for (int j = 0; j < xj; j++) {
                int index = x_i + j;
                PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            int x_i = i * xj;
            for (int j = 0; j < xj; j++) {
                int index = x_i + j;
                PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[j]);
            }
        }
    }
}

template<Operation Op>
inline void zipWithD2ToD3(
        JNIEnv *env, jobject obj,
        jobject x, jint xi, jint xj,
        jobject y, jint yi, jint yj, jint yk,
        jint axis1, jint axis2,
        jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[i * yj + j];
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int y_index = y_i + k;
                    PERFORM_OPERATION(result_ptr[y_index], x_value, y_ptr[y_index]);
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int x_i = i * yk;
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int x_index = x_i + k;
                    int y_index = y_i + k;
                    PERFORM_OPERATION(result_ptr[y_index], x_ptr[x_index], y_ptr[y_index]);
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int x_i = j * yk;
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int x_index = x_i + k;
                    int y_index = y_i + k;
                    PERFORM_OPERATION(result_ptr[y_index], x_ptr[x_index], y_ptr[y_index]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD3ToD1(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk,
    jobject y,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[k]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD3ToD2(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk,
    jobject y, jint yi, jint yj,
    jint axis1, jint axis2,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                float y_value = y_ptr[i * yj + j];
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    int y_index = i * yj + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    int y_index = j * yj + k;
                    PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD3ToD4(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk,
    jobject y, jint yi, jint yj, jint yk, jint yl,
    jint axis1, jint axis2, jint axis3,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    float x_value = x_ptr[(i * xj + j) * xk + k];
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[y_index], x_value, y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int x_index = (i * xj + j) * xk + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[y_index], x_ptr[x_index], y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int x_index = (i * xj + k) * xk + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[y_index], x_ptr[x_index], y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int x_index = (j * xj + k) * xk + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[y_index], x_ptr[x_index], y_ptr[y_index]);
                    }
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD4ToD1(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk, jint xl,
    jobject y,
    jint axis,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[k];
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[l]);
                    }
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD4ToD2(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk, jint xl,
    jobject y, jint yi, jint yj,
    jint axis1, jint axis2,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yj + j];
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[i * yj + k];
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = i * yj + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[j * yj + k];
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = j * yj + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 2 && axis2 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = k * yj + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                    }
                }
            }
        }
    }
}

template<Operation Op>
inline void zipWithD4ToD3(
    JNIEnv *env, jobject obj,
    jobject x, jint xi, jint xj, jint xk, jint xl,
    jobject y, jint yi, jint yj, jint yk,
    jint axis1, jint axis2, jint axis3,
    jobject result
) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    float y_value = y_ptr[(i * yj + j) * yk + k];
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    int y_i = (i * yj + j) * yk;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    int y_i = (i * yj + k) * yk;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    int y_i = (j * yj + k) * yk;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result_ptr[index], x_ptr[index], y_ptr[y_index]);
                    }
                }
            }
        }
    }
}

// ========== PLUS ==========

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD0ToD1
(JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    zipWithD0ToD1<Operation::Plus>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD0
(JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    zipWithD1ToD0<Operation::Plus>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD1
(JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    zipWithD1ToD1<Operation::Plus>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD2
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    zipWithD1ToD2<Operation::Plus>(env, obj, x, y, yi, yj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD3
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    zipWithD1ToD3<Operation::Plus>(env, obj, x, y, yi, yj, yk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD2ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    zipWithD2ToD1<Operation::Plus>(env, obj, x, xi, xj, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD2ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    zipWithD2ToD3<Operation::Plus>(env, obj, x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    zipWithD3ToD1<Operation::Plus>(env, obj, x, xi, xj, xk, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    zipWithD3ToD2<Operation::Plus>(env, obj, x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD3ToD4<Operation::Plus>(env, obj, x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    zipWithD4ToD1<Operation::Plus>(env, obj, x, xi, xj, xk, xl, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    zipWithD4ToD2<Operation::Plus>(env, obj, x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD4ToD3<Operation::Plus>(env, obj, x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}

// ========== MINUS 演算の実装 ==========

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD0ToD1
  (JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    zipWithD0ToD1<Operation::Minus>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD0
  (JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    zipWithD1ToD0<Operation::Minus>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD1
  (JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    zipWithD1ToD1<Operation::Minus>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    zipWithD1ToD2<Operation::Minus>(env, obj, x, y, yi, yj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD3
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    zipWithD1ToD3<Operation::Minus>(env, obj, x, y, yi, yj, yk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    zipWithD2ToD1<Operation::Minus>(env, obj, x, xi, xj, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD2ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    zipWithD2ToD3<Operation::Minus>(env, obj, x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    zipWithD3ToD1<Operation::Minus>(env, obj, x, xi, xj, xk, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
   zipWithD3ToD2<Operation::Minus>(env, obj, x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD3ToD4<Operation::Minus>(env, obj, x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    zipWithD4ToD1<Operation::Minus>(env, obj, x, xi, xj, xk, xl, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    zipWithD4ToD2<Operation::Minus>(env, obj, x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD4ToD3<Operation::Minus>(env, obj, x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}

// ========== TIMES 演算の実装 ==========

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD0ToD1
  (JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    zipWithD0ToD1<Operation::Times>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD0
  (JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    zipWithD1ToD0<Operation::Times>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD1
  (JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    zipWithD1ToD1<Operation::Times>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    zipWithD1ToD2<Operation::Times>(env, obj, x, y, yi, yj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD3
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    zipWithD1ToD3<Operation::Times>(env, obj, x, y, yi, yj, yk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    zipWithD2ToD1<Operation::Times>(env, obj, x, xi, xj, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD2ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    zipWithD2ToD3<Operation::Times>(env, obj, x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    zipWithD3ToD1<Operation::Times>(env, obj, x, xi, xj, xk, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
   zipWithD3ToD2<Operation::Times>(env, obj, x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD3ToD4<Operation::Times>(env, obj, x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    zipWithD4ToD1<Operation::Times>(env, obj, x, xi, xj, xk, xl, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    zipWithD4ToD2<Operation::Times>(env, obj, x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD4ToD3<Operation::Times>(env, obj, x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}

// ========== DIV 演算の実装 ==========

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD0ToD1
  (JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    zipWithD0ToD1<Operation::Div>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD0
  (JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    zipWithD1ToD0<Operation::Div>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD1
  (JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    zipWithD1ToD1<Operation::Div>(env, obj, x, y, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    zipWithD1ToD2<Operation::Div>(env, obj, x, y, yi, yj, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD3
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    zipWithD1ToD3<Operation::Div>(env, obj, x, y, yi, yj, yk, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    zipWithD2ToD1<Operation::Div>(env, obj, x, xi, xj, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD2ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    zipWithD2ToD3<Operation::Div>(env, obj, x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    zipWithD3ToD1<Operation::Div>(env, obj, x, xi, xj, xk, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
   zipWithD3ToD2<Operation::Div>(env, obj, x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD3ToD4<Operation::Div>(env, obj, x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    zipWithD4ToD1<Operation::Div>(env, obj, x, xi, xj, xk, xl, y, axis, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    zipWithD4ToD2<Operation::Div>(env, obj, x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    zipWithD4ToD3<Operation::Div>(env, obj, x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}
