#include "com_wsr_cpu_JTranspose.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD2(
        JNIEnv *env, jobject, jobject x, jint xi, jint xj, jobject result
) {
    // Javaからポインタを取得
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    for (int i = 0; i < xi; i++) {
        for (int j = 0; j < xj; j++) {
            result_ptr[j * xi + i] = x_ptr[i * xj + j];
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD3(
        JNIEnv *env, jobject, jobject x, jint xi, jint xj, jint xk,
        jint axis_i, jint axis_j, jint axis_k, jobject result
) {
    // Javaからポインタを取得
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    int old_shape[3] = { xi, xj, xk };
    int new_shape[3] = { old_shape[axis_i], old_shape[axis_j], old_shape[axis_k] };

    for (int ni = 0; ni < new_shape[0]; ni++) {
        int nii = ni * new_shape[1];
        for (int nj = 0; nj < new_shape[1]; nj++) {
            int nji = (nii + nj) * new_shape[2];
            for (int nk = 0; nk < new_shape[2]; nk++) {
                int new_index = nji + nk;

                int old_indexes[3] = { 0 };
                old_indexes[axis_i] = ni;
                old_indexes[axis_j] = nj;
                old_indexes[axis_k] = nk;
                int old_index = (old_indexes[0] * xj + old_indexes[1]) * xk + old_indexes[2];

                result_ptr[new_index] = x_ptr[old_index];
            }
        }
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD4(
        JNIEnv *env, jobject, jobject x, jint xi, jint xj, jint xk, jint xl,
        jint axis_i, jint axis_j, jint axis_k, jint axis_l, jobject result
) {
    // Javaからポインタを取得
    jfloat *x_ptr = (jfloat*)env->GetDirectBufferAddress(x);
    jfloat *result_ptr = (jfloat*)env->GetDirectBufferAddress(result);

    int old_shape[4] = { xi, xj, xk, xl };
    int new_shape[4] = { old_shape[axis_i], old_shape[axis_j], old_shape[axis_k], old_shape[axis_l] };

    for (int ni = 0; ni < new_shape[0]; ni++) {
        int nii = ni * new_shape[1];
        for (int nj = 0; nj < new_shape[1]; nj++) {
            int nji = (nii + nj) * new_shape[2];
            for (int nk = 0; nk < new_shape[2]; nk++) {
                int nki = (nji + nk) * new_shape[3];
                for (int nl = 0; nl < new_shape[3]; nl++) {
                    int new_index = nki + nl;

                    int old_indexes[4] = { 0 };
                    old_indexes[axis_i] = ni;
                    old_indexes[axis_j] = nj;
                    old_indexes[axis_k] = nk;
                    old_indexes[axis_l] = nl;
                    int old_index = ((old_indexes[0] * xj + old_indexes[1]) * xk + old_indexes[2]) * xl + old_indexes[3];

                    result_ptr[new_index] = x_ptr[old_index];
                }
            }
        }
    }
}
