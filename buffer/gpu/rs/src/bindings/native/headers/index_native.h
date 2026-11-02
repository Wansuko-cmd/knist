#ifndef INDEX_NATIVE_H
#define INDEX_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_gather(const GPUBuffer* x, const GPUBuffer* y, int i, int j, int k, const GPUBuffer* result, Runtime* runtime);
void com_wsr_gpu_scatter_add(const GPUBuffer* x, const GPUBuffer* y, int i, int j, int k, int b, const GPUBuffer* result, Runtime* runtime);

#endif
