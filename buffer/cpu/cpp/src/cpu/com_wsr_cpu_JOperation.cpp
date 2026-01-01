#include "com_wsr_cpu_JOperation.h"
#include <stdio.h>
#include <cmath>

// ========== PLUS ==========

// 0次元から1次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD0ToD1
(JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(y) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x + y_ptr[i];
    }
}

// 1次元から0次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD0
(JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] + y;
    }
}

// 1次元と1次元の加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD1
(JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] + y_ptr[i];
    }
}

// 1次元から2次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD2
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行にx[i]を加算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_value + y_ptr[idx];
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列にx[j]を加算
        for (int i = 0; i < yi; i++) {
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[j] + y_ptr[idx];
            }
        }
    }
}

// 1次元から3次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD1ToD3
(JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面にx[i]を加算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value + y_ptr[idx];
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面にx[j]を加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value + y_ptr[idx];
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素にx[k]を加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[k] + y_ptr[idx];
                }
            }
        }
    }
}

// 2次元から1次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD2ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行のすべての要素にy[i]を加算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] + y_value;
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列のすべての要素にy[j]を加算
        for (int i = 0; i < xi; i++) {
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] + y_ptr[j];
            }
        }
    }
}

// 2次元から3次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD2ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[i * xj + j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value + y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[i * xj + k];
                    result_ptr[idx] = x_value + y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[j * xj + k];
                    result_ptr[idx] = x_value + y_ptr[idx];
                }
            }
        }
    }
}

// 3次元から1次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat * x_ptr = (jfloat *) env->GetDirectBufferAddress(x);
    jfloat * y_ptr = (jfloat *) env->GetDirectBufferAddress(y);
    jfloat * result_ptr = (jfloat *) env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面の全要素にy[i]を加算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr [i];
            for (int j = 0; j < xj; j++) {
                int offset =(i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset +k;
                    result_ptr[idx] = x_ptr[idx] + y_value;
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面の全要素にy[j]を加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr [j];
                int offset =(i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset +k;
                    result_ptr[idx] = x_ptr[idx] + y_value;
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素にy[k]を加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset =(i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset +k;
                    result_ptr[idx] = x_ptr[idx] + y_ptr[k];
                }
            }
        }
    }
}

// 3次元から2次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] + y_value;
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] + y_ptr[i * yi + k];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] + y_ptr[j * yj + k];
                }
            }
        }
    }
}

// 3次元から4次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に対応する要素を加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    float x_value = x_ptr[(i * xj + j) * xk + k];
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_value + y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に対応する要素を加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + j) * xk + l];
                        result_ptr[idx] = x_value + y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に対応する要素を加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + k) * xk + l];
                        result_ptr[idx] = x_value + y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に対応する要素を加算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(j * xj + k) * xk + l];
                        result_ptr[idx] = x_value + y_ptr[idx];
                    }
                }
            }
        }
    }
}

// 4次元から1次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: i次元に沿ってy[i]を加算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: j次元に沿ってy[j]を加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: k次元に沿ってy[k]を加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis == 3) {
        // axis 3: l次元に沿ってy[l]を加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[l];
                    }
                }
            }
        }
    }
}

// 4次元から2次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[i * yi + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 3) {
        // (i,l)平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[i * yi + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[j * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 3) {
        // (j,l)平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[j * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 2 && axis2 == 3) {
        // (k,l)平面に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[k * yj + l];
                    }
                }
            }
        }
    }
}

// 4次元から3次元への加算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_plusD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[(i * yi + j) * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[(i * yi + j) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[(i * yi + k) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に沿って加算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] + y_ptr[(j * yi + k) * yj + l];
                    }
                }
            }
        }
    }
}

// ========== MINUS 演算の実装 ==========

// 0次元から1次元への減算（スカラー - 配列）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD0ToD1
  (JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(y) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x - y_ptr[i];
    }
}

// 1次元から0次元への減算（配列 - スカラー）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD0
  (JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] - y;
    }
}

// 1次元と1次元の減算（配列 - 配列）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD1
  (JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] - y_ptr[i];
    }
}

// 1次元から2次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行にx[i]を減算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_value - y_ptr[idx];
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列にx[j]を減算
        for (int i = 0; i < yi; i++) {
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[j] - y_ptr[idx];
            }
        }
    }
}

// 1次元から3次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD1ToD3
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面にx[i]を減算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value - y_ptr[idx];
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面にx[j]を減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value - y_ptr[idx];
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素にx[k]を減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[k] - y_ptr[idx];
                }
            }
        }
    }
}

// 2次元から1次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行からy[i]を減算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] - y_value;
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列からy[j]を減算
        for (int i = 0; i < xi; i++) {
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] - y_ptr[j];
            }
        }
    }
}

// 2次元から3次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD2ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[i * xj + j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value - y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[i * xj + k];
                    result_ptr[idx] = x_value - y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[j * xj + k];
                    result_ptr[idx] = x_value - y_ptr[idx];
                }
            }
        }
    }
}

// 3次元から1次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面からy[i]を減算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] - y_value;
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面からy[j]を減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] - y_value;
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素からy[k]を減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] - y_ptr[k];
                }
            }
        }
    }
}

// 3次元から2次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD2
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
// (i,j)の平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
            float y_value = y_ptr[i * yi + j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] - y_value;
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] - y_ptr[i * yi + k];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] - y_ptr[j * yj + k];
                }
            }
        }
    }
}

// 3次元から4次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に対応する要素を減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    float x_value = x_ptr[(i * xj + j) * xk + k];
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_value - y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に対応する要素を減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + j) * xk + l];
                        result_ptr[idx] = x_value - y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に対応する要素を減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + k) * xk + l];
                        result_ptr[idx] = x_value - y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に対応する要素を減算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(j * xj + k) * xk + l];
                        result_ptr[idx] = x_value - y_ptr[idx];
                    }
                }
            }
        }
    }
}

// 4次元から1次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: i次元に沿ってy[i]を減算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: j次元に沿ってy[j]を減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: k次元に沿ってy[k]を減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis == 3) {
        // axis 3: l次元に沿ってy[l]を減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[l];
                    }
                }
            }
        }
    }
}

// 4次元から2次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[i * yi + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 3) {
        // (i,l)平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[i * yi + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[j * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 3) {
        // (j,l)平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[j * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 2 && axis2 == 3) {
        // (k,l)平面に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[k * yj + l];
                    }
                }
            }
        }
    }
}

// 4次元から3次元への減算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_minusD4ToD3
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[(i * yi + j) * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[(i * yi + j) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[(i * yi + k) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に沿って減算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] - y_ptr[(j * yi + k) * yj + l];
                    }
                }
            }
        }
    }
}

// ========== TIMES 演算の実装 ==========

// 0次元から1次元への乗算（スカラー * 配列）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD0ToD1
  (JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(y) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x * y_ptr[i];
    }
}

// 1次元から0次元への乗算（配列 * スカラー）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD0
  (JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] * y;
    }
}

// 1次元と1次元の乗算（配列 * 配列）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD1
  (JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] * y_ptr[i];
    }
}

// 1次元から2次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行にx[i]を乗算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_value * y_ptr[idx];
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列にx[j]を乗算
        for (int i = 0; i < yi; i++) {
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[j] * y_ptr[idx];
            }
        }
    }
}

// 1次元から3次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD1ToD3
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面にx[i]を乗算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value * y_ptr[idx];
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面にx[j]を乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value * y_ptr[idx];
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素にx[k]を乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[k] * y_ptr[idx];
                }
            }
        }
    }
}

// 2次元から1次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行にy[i]を乗算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] * y_value;
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列にy[j]を乗算
        for (int i = 0; i < xi; i++) {
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] * y_ptr[j];
            }
        }
    }
}

// 2次元から3次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD2ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[i * xj + j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value * y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[i * xj + k];
                    result_ptr[idx] = x_value * y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[j * xj + k];
                    result_ptr[idx] = x_value * y_ptr[idx];
                }
            }
        }
    }
}

// 3次元から1次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面にy[i]を乗算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] * y_value;
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面にy[j]を乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] * y_value;
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素にy[k]を乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] * y_ptr[k];
                }
            }
        }
    }
}

// 3次元から2次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] * y_value;
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] * y_ptr[i * yi + k];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] * y_ptr[j * yj + k];
                }
            }
        }
    }
}

// 3次元から4次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に対応する要素を乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    float x_value = x_ptr[(i * xj + j) * xk + k];
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_value * y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に対応する要素を乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + j) * xk + l];
                        result_ptr[idx] = x_value * y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に対応する要素を乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + k) * xk + l];
                        result_ptr[idx] = x_value * y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に対応する要素を乗算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(j * xj + k) * xk + l];
                        result_ptr[idx] = x_value * y_ptr[idx];
                    }
                }
            }
        }
    }
}

// 4次元から1次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: i次元に沿ってy[i]を乗算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: j次元に沿ってy[j]を乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: k次元に沿ってy[k]を乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis == 3) {
        // axis 3: l次元に沿ってy[l]を乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[l];
                    }
                }
            }
        }
    }
}

// 4次元から2次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[i * yi + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 3) {
        // (i,l)平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[i * yi + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[j * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 3) {
        // (j,l)平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[j * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 2 && axis2 == 3) {
        // (k,l)平面に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[k * yj + l];
                    }
                }
            }
        }
    }
}

// 4次元から3次元への乗算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_timesD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[(i * yi + j) * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[(i * yi + j) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[(i * yi + k) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に沿って乗算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] * y_ptr[(j * yi + k) * yj + l];
                    }
                }
            }
        }
    }
}

// ========== DIV 演算の実装 ==========

// 0次元から1次元への除算（スカラー / 配列）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD0ToD1
  (JNIEnv *env, jobject obj, jfloat x, jobject y, jobject result) {
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(y) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x / y_ptr[i];
    }
}

// 1次元から0次元への除算（配列 / スカラー）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD0
  (JNIEnv *env, jobject obj, jobject x, jfloat y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] / y;
    }
}

// 1次元と1次元の除算（配列 / 配列）
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD1
  (JNIEnv *env, jobject obj, jobject x, jobject y, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);
    jlong size = env->GetDirectBufferCapacity(x) / sizeof(jfloat);

    for (int i = 0; i < size; i++) {
        result_ptr[i] = x_ptr[i] / y_ptr[i];
    }
}

// 1次元から2次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD2
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行のx[i]で除算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_value / y_ptr[idx];
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列のx[j]で除算
        for (int i = 0; i < yi; i++) {
            int offset = i * yj;
            for (int j = 0; j < yj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[j] / y_ptr[idx];
            }
        }
    }
}

// 1次元から3次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD1ToD3
  (JNIEnv *env, jobject obj, jobject x, jobject y, jint yi, jint yj, jint yk, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面のx[i]で除算
        for (int i = 0; i < yi; i++) {
            float x_value = x_ptr[i];
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value / y_ptr[idx];
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面のx[j]で除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value / y_ptr[idx];
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素のx[k]で除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[k] / y_ptr[idx];
                }
            }
        }
    }
}

// 2次元から1次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD2ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各行をy[i]で除算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] / y_value;
            }
        }
    } else if (axis == 1) {
        // axis 1: 各列をy[j]で除算
        for (int i = 0; i < xi; i++) {
            int offset = i * xj;
            for (int j = 0; j < xj; j++) {
                int idx = offset + j;
                result_ptr[idx] = x_ptr[idx] / y_ptr[j];
            }
        }
    }
}

// 2次元から3次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD2ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x_ptr[i * xj + j];
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_value / y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[i * xj + k];
                    result_ptr[idx] = x_value / y_ptr[idx];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int offset = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int idx = offset + k;
                    float x_value = x_ptr[j * xj + k];
                    result_ptr[idx] = x_value / y_ptr[idx];
                }
            }
        }
    }
}

// 3次元から1次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD1
(JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: 各i平面をy[i]で除算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] / y_value;
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: 各j平面をy[j]で除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] / y_value;
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: 各k要素をy[k]で除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] / y_ptr[k];
                }
            }
        }
    }
}

// 3次元から2次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)の平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] / y_value;
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)の平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] / y_ptr[i * yi + k];
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)の平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int offset = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int idx = offset + k;
                    result_ptr[idx] = x_ptr[idx] / y_ptr[j * yj + k];
                }
            }
        }
    }
}

// 3次元から4次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD3ToD4
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jobject y, jint yi, jint yj, jint yk, jint yl, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に対応する要素を除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    float x_value = x_ptr[(i * xj + j) * xk + k];
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_value / y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に対応する要素を除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + j) * xk + l];
                        result_ptr[idx] = x_value / y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に対応する要素を除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(i * xj + k) * xk + l];
                        result_ptr[idx] = x_value / y_ptr[idx];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に対応する要素を除算
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int offset = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int idx = offset + l;
                        float x_value = x_ptr[(j * xj + k) * xk + l];
                        result_ptr[idx] = x_value / y_ptr[idx];
                    }
                }
            }
        }
    }
}

// 4次元から1次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD1
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint axis, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis == 0) {
        // axis 0: i次元に沿ってy[i]で除算
        for (int i = 0; i < xi; i++) {
            float y_value = y_ptr[i];
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis == 1) {
        // axis 1: j次元に沿ってy[j]で除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis == 2) {
        // axis 2: k次元に沿ってy[k]で除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis == 3) {
        // axis 3: l次元に沿ってy[l]で除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[l];
                    }
                }
            }
        }
    }
}

// 4次元から2次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD2
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint axis1, jint axis2, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1) {
        // (i,j)平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y_ptr[i * yi + j];
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        // (i,k)平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[i * yi + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 3) {
        // (i,l)平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[i * yi + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        // (j,k)平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[j * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 3) {
        // (j,l)平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[j * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 2 && axis2 == 3) {
        // (k,l)平面に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[k * yj + l];
                    }
                }
            }
        }
   }
}

// 4次元から3次元への除算
JNIEXPORT void JNICALL Java_com_wsr_cpu_JOperation_divD4ToD3
  (JNIEnv *env, jobject obj, jobject x, jint xi, jint xj, jint xk, jint xl, jobject y, jint yi, jint yj, jint yk, jint axis1, jint axis2, jint axis3, jobject result) {
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *y_ptr = (jfloat*)env->GetDirectBufferAddress(y);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        // (i,j,k)の3次元に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y_ptr[(i * yi + j) * yj + k];
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_value;
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        // (i,j,l)の3次元に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[(i * yi + j) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        // (i,k,l)の3次元に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[(i * yi + k) * yj + l];
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        // (j,k,l)の3次元に沿って除算
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int offset = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int idx = offset + l;
                        result_ptr[idx] = x_ptr[idx] / y_ptr[(j * yi + k) * yj + l];
                    }
                }
            }
        }
    }
}