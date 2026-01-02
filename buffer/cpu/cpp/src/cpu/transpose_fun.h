#ifndef TRANSPOSE_FUN_H
#define TRANSPOSE_FUN_H

#ifdef __cplusplus
extern "C" {
#endif

void transpose_d2(const float* x, int xi, int xj, float* result);

void transpose_d3(
    const float* x,
    int xi, int xj, int xk,
    int axis_i, int axis_j, int axis_k,
    float* result
);

void transpose_d4(
    const float* x,
    int xi, int xj, int xk, int xl,
    int axis_i, int axis_j, int axis_k, int axis_l,
    float* result
);

#ifdef __cplusplus
}
#endif
#endif
