#ifndef OPERATION_NATIVE_H
#define OPERATION_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_div_d0_to_d1(float x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d1_to_d0(const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d1_to_d2(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d1_to_d3(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int yk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d2_to_d1(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d2_to_d3(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d3_to_d1(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d3_to_d2(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d3_to_d4(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int yk, int yl, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d4_to_d1(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d4_to_d2(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_div_d4_to_d3(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d0_to_d1(float x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d1_to_d0(const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d1_to_d2(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d1_to_d3(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int yk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d2_to_d1(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d2_to_d3(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d3_to_d1(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d3_to_d2(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d3_to_d4(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int yk, int yl, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d4_to_d1(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d4_to_d2(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_minus_d4_to_d3(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d0_to_d1(float x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d1_to_d0(const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d1_to_d2(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d1_to_d3(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int yk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d2_to_d1(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d2_to_d3(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d3_to_d1(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d3_to_d2(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d3_to_d4(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int yk, int yl, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d4_to_d1(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d4_to_d2(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_plus_d4_to_d3(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d0_to_d1(float x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d1_to_d0(const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d1_to_d2(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d1_to_d3(const GPUBuffer* x, const GPUBuffer* y, int yi, int yj, int yk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d2_to_d1(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d2_to_d3(const GPUBuffer* x, int xi, int xj, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d3_to_d1(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d3_to_d2(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d3_to_d4(const GPUBuffer* x, int xi, int xj, int xk, const GPUBuffer* y, int yi, int yj, int yk, int yl, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d4_to_d1(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d4_to_d2(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int axis1, int axis2, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_times_d4_to_d3(const GPUBuffer* x, int xi, int xj, int xk, int xl, const GPUBuffer* y, int yi, int yj, int yk, int axis1, int axis2, int axis3, const GPUBuffer* result, Runtime* runtime);

#endif
