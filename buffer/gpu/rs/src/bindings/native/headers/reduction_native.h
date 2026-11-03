#ifndef REDUCTION_NATIVE_H
#define REDUCTION_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_average_d1(const GPUBuffer* x, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_average_d2(const GPUBuffer* x, int xi, int xj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_average_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_max_d1(const GPUBuffer* x, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_max_d2(const GPUBuffer* x, int xi, int xj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_max_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_min_d1(const GPUBuffer* x, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_min_d2(const GPUBuffer* x, int xi, int xj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_min_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_sum_d1(const GPUBuffer* x, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_sum_d2(const GPUBuffer* x, int xi, int xj, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_sum_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis, const GPUBuffer* result, Runtime* runtime);

#endif
