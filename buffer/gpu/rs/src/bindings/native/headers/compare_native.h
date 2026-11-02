#ifndef COMPARE_NATIVE_H
#define COMPARE_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_greater_than_d1_to_d0(const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_greater_than_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_less_than_d1_to_d0(const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_less_than_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_equals_d1_to_d0(const GPUBuffer* x, float y, float atol, float rtol, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_equals_d1_to_d1(const GPUBuffer* x, const GPUBuffer* y, float atol, float rtol, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_where_d0_to_d0(const GPUBuffer* condition, float x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_where_d0_to_d1(const GPUBuffer* condition, float x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_where_d1_to_d0(const GPUBuffer* condition, const GPUBuffer* x, float y, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_where_d1_to_d1(const GPUBuffer* condition, const GPUBuffer* x, const GPUBuffer* y, const GPUBuffer* result, Runtime* runtime);

#endif
