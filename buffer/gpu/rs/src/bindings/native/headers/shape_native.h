#ifndef SHAPE_NATIVE_H
#define SHAPE_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_transpose_d2(const GPUBuffer* x, int xi, int xj, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_transpose_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis_i, int axis_j, int axis_k, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_transpose_d4(const GPUBuffer* x, int xi, int xj, int xk, int xl, int axis_i, int axis_j, int axis_k, int axis_l, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_slice_d1(const GPUBuffer* x, int start, int end, int step, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_slice_d2(const GPUBuffer* x, int xi, int xj, int axis, int start, int end, int step, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_slice_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis, int start, int end, int step, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_copy_into_d1(const GPUBuffer* x, const GPUBuffer* result, int start, int end, int step, Runtime* runtime);
void com_wsr_gpu_copy_into_d2(const GPUBuffer* x, const GPUBuffer* result, int ri, int rj, int axis, int start, int end, int step, Runtime* runtime);
void com_wsr_gpu_copy_into_d3(const GPUBuffer* x, const GPUBuffer* result, int ri, int rj, int rk, int axis, int start, int end, int step, Runtime* runtime);
void com_wsr_gpu_flip_d3(const GPUBuffer* x, int xi, int xj, int xk, int axis, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_unfold_d1(const GPUBuffer* x, int xi, int xj, int b, int window, int stride, int dilation, int padding, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_unfold_d2(const GPUBuffer* x, int xi, int xj, int xk, int b, int window, int stride, int dilation, int padding, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_fold_d1(const GPUBuffer* x, int xi, int xj, int xk, int b, int stride, int dilation, int padding, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_fold_d2(const GPUBuffer* x, int xi, int xj, int xk, int xl, int b, int stride, int dilation, int padding, const GPUBuffer* result, Runtime* runtime);

#endif
