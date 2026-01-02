#include <transpose_fun.h>

#ifdef _OPENMP
#include <omp.h>
#endif

// L1キャッシュを32KB = 4096Bと仮定
const int PARALLEL_THRESHOLD = 4 * 4096;

void transpose_d2(const float* x, int xi, int xj, float* result) {
    #ifdef _OPENMP
    #pragma omp parallel for collapse(2) if(xi * xj >= PARALLEL_THRESHOLD)
    #endif
    for (int i = 0; i < xi; i++) {
        for (int j = 0; j < xj; j++) {
            result[j * xi + i] = x[i * xj + j];
        }
    }
}

void transpose_d3(
    const float* x,
    int xi, int xj, int xk,
    int axis_i, int axis_j, int axis_k,
    float* result
) {
    int old_shape[3] = { xi, xj, xk };
    int new_shape[3] = { old_shape[axis_i], old_shape[axis_j], old_shape[axis_k] };

    #ifdef _OPENMP
    #pragma omp parallel for if(xi * xj * xk >= PARALLEL_THRESHOLD)
    #endif
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

                result[new_index] = x[old_index];
            }
        }
    }
}

void transpose_d4(
    const float* x,
    int xi, int xj, int xk, int xl,
    int axis_i, int axis_j, int axis_k, int axis_l,
    float* result
) {
    int old_shape[4] = { xi, xj, xk, xl };
    int new_shape[4] = { old_shape[axis_i], old_shape[axis_j], old_shape[axis_k], old_shape[axis_l] };

    #ifdef _OPENMP
    #pragma omp parallel for if(xi * xj * xk * xl >= PARALLEL_THRESHOLD)
    #endif
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

                    result[new_index] = x[old_index];
                }
            }
        }
    }
}
