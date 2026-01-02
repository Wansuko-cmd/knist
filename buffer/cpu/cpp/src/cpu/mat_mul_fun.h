#ifndef MAT_MUL_FUN_H
#define MAT_MUL_FUN_H

#include <cstddef>

#ifdef __cplusplus
extern "C" {
#endif

void inner(float* x, float* y, size_t size, int b, float* result);

void mat_mul_d1_to_d2(
    const float* x,
    const float* y, bool trans_y,
    int n, int k,
    float* result
);

void mat_mul_d2_to_d1(
    const float* x, bool trans_x,
    const float* y,
    int m, int k,
    float* result
);

void mat_mul_d2_to_d2(
    float* x, bool trans_x,
    float* y, bool trans_y,
    int m, int n, int k,
    int b,
    float* result
);

#ifdef __cplusplus
}
#endif
#endif
