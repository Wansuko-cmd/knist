#ifndef MATH_NATIVE_H
#define MATH_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_exp(const GPUBuffer* x, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_ln(const GPUBuffer* x, float e, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_sigmoid(const GPUBuffer* x, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_pow(const GPUBuffer* x, int n, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_sqrt(const GPUBuffer* x, float e, const GPUBuffer* result, Runtime* runtime);

#endif
