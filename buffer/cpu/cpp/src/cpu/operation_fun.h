#ifndef OPERATION_FUN_H
#define OPERATION_FUN_H

#include <cstddef>

#ifdef __cplusplus
extern "C" {
#endif

void plus_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
);

void plus_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
);

void plus_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
);

void plus_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
);

void plus_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
);

void plus_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
);

void plus_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
);

void plus_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
);

void plus_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void plus_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
);

void plus_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
);

void plus_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void plus_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
);

void minus_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
);

void minus_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
);

void minus_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
);

void minus_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
);

void minus_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
);

void minus_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
);

void minus_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
);

void minus_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
);

void minus_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void minus_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
);

void minus_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
);

void minus_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void minus_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
);

void times_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
);

void times_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
);

void times_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
);

void times_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
);

void times_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
);

void times_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
);

void times_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
);

void times_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
);

void times_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void times_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
);

void times_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
);

void times_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void times_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
);

void div_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
);

void div_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
);

void div_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
);

void div_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
);

void div_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
);

void div_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
);

void div_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
);

void div_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
);

void div_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void div_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
);

void div_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
);

void div_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
);

void div_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
);

#ifdef __cplusplus
}
#endif
#endif
