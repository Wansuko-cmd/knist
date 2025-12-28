#include "com_wsr_cpu_JTranspose.h"
#include <algorithm>
#include <stdio.h>

// L1キャッシュを32KB = 4096Bと仮定
const int BLOCK_THRESHOLD = 4096;

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD2(
        JNIEnv *env, jobject, jfloatArray x, jint xi, jint xj, jfloatArray result
) {
    // Javaからポインタを取得
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *result_ptr = env->GetFloatArrayElements(result, nullptr);

    if (xi * xj < BLOCK_THRESHOLD) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                result_ptr[j * xi + i] = x_ptr[i * xj + j];
            }
        }
    } else {
        // 4byte * 32 * 32 = 4096B
        const int BLOCK_SIZE = 32;
        for (int i_block = 0; i_block < xi; i_block += BLOCK_SIZE) {
            for (int j_block = 0; j_block < xj; j_block += BLOCK_SIZE) {
                int i_end = std::min(i_block + BLOCK_SIZE, static_cast<int>(xi));
                int j_end = std::min(j_block + BLOCK_SIZE, static_cast<int>(xj));
                for (int i = i_block; i < i_end; i++) {
                    for (int j = j_block; j < j_end; j++) {
                        result_ptr[j * xi + i] = x_ptr[i * xj + j];
                    }
                }
            }
        }
    }

    // リソース解放
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(result, result_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD3(
        JNIEnv *env, jobject, jfloatArray x, jint xi, jint xj, jint xk,
        jint axis_i, jint axis_j, jint axis_k, jfloatArray result
) {
    // Javaからポインタを取得
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *result_ptr = env->GetFloatArrayElements(result, nullptr);

    int old_shape[3] = { xi, xj, xk };
    int new_shape[3] = { old_shape[axis_i], old_shape[axis_j], old_shape[axis_k] };

    if (xi * xj * xk < BLOCK_THRESHOLD) {
        for (int ni = 0; ni < new_shape[0]; ni++) {
            int nii = ni * new_shape[1];
            for (int nj = 0; nj < new_shape[1]; nj++) {
                int nji = (nii + nj) * new_shape[2];
                for (int nk = 0; nk < new_shape[2]; nk++) {
                    int new_index = nji + nk;

                    int old_indexes[3] = {0};
                    old_indexes[axis_i] = ni;
                    old_indexes[axis_j] = nj;
                    old_indexes[axis_k] = nk;
                    int old_index = (old_indexes[0] * xj + old_indexes[1]) * xk + old_indexes[2];

                    result_ptr[new_index] = x_ptr[old_index];
                }
            }
        }
    } else {
        // 4byte * 16 * 16 * 16 = 4096B
        const int BLOCK_SIZE = 16;
        for (int i_block = 0; i_block < new_shape[0]; i_block += BLOCK_SIZE) {
            for (int j_block = 0; j_block < new_shape[1]; j_block += BLOCK_SIZE) {
                for (int k_block = 0; k_block < new_shape[2]; k_block += BLOCK_SIZE) {
                    int i_end = std::min(i_block + BLOCK_SIZE, new_shape[0]);
                    int j_end = std::min(j_block + BLOCK_SIZE, new_shape[1]);
                    int k_end = std::min(k_block + BLOCK_SIZE, new_shape[2]);

                    for (int ni = i_block; ni < i_end; ni++) {
                        int nii = ni * new_shape[1];
                        for (int nj = j_block; nj < j_end; nj++) {
                            int nji = (nii + nj) * new_shape[2];
                            for (int nk = k_block; nk < k_end; nk++) {
                                int new_index = nji + nk;

                                int old_indexes[3] = {0};
                                old_indexes[axis_i] =ni;
                                old_indexes[axis_j] =nj;
                                old_indexes[axis_k] =nk;
                                int old_index = (old_indexes[0] * xj + old_indexes[1]) * xk + old_indexes[2];

                                result_ptr[new_index] = x_ptr[old_index];
                            }
                        }
                    }
                }
             }
        }
    }


    // リソース解放
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(result, result_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_cpu_JTranspose_transposeD4(
        JNIEnv *env, jobject, jfloatArray x, jint xi, jint xj, jint xk, jint xl,
        jint axis_i, jint axis_j, jint axis_k, jint axis_l, jfloatArray result
) {
    // Javaからポインタを取得
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *result_ptr = env->GetFloatArrayElements(result, nullptr);

    int old_shape[4] = { xi, xj, xk, xl };
    int new_shape[4] = { old_shape[axis_i], old_shape[axis_j], old_shape[axis_k], old_shape[axis_l] };

    if (xi * xj * xk * xl < BLOCK_THRESHOLD) {
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
    } else {
        // 4byte * 8 * 8 * 8 * 8 = 4096B
        const int BLOCK_SIZE = 8;
        for (int i_block = 0; i_block < new_shape[0]; i_block += BLOCK_SIZE) {
            for (int j_block = 0; j_block < new_shape[1]; j_block += BLOCK_SIZE) {
                for (int k_block = 0; k_block < new_shape[2]; k_block += BLOCK_SIZE) {
                    for (int l_block = 0; l_block < new_shape[3]; l_block += BLOCK_SIZE) {
                        int i_end = std::min(i_block + BLOCK_SIZE, new_shape[0]);
                        int j_end = std::min(j_block + BLOCK_SIZE, new_shape[1]);
                        int k_end = std::min(k_block + BLOCK_SIZE, new_shape[2]);
                        int l_end = std::min(l_block + BLOCK_SIZE, new_shape[3]);

                        for (int ni = i_block; ni < i_end; ni++) {
                            int nii = ni * new_shape[1];
                            for (int nj = j_block; nj < j_end; nj++) {
                                int nji = (nii + nj) * new_shape[2];
                                for (int nk = k_block; nk < k_end; nk++) {
                                    int nki = (nji + nk) * new_shape[3];
                                    for (int nl = l_block; nl < l_end; nl++) {
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
                }
            }
        }
    }


    // リソース解放
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(result, result_ptr, 0);
}
