#ifndef MAT_MUL_NATIVE_H
#define MAT_MUL_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_mat_mul(const GPUBuffer* x, bool trans_x, const GPUBuffer* y, bool trans_y, int m, int n, int k, int b, const GPUBuffer* result, Runtime* runtime);

#endif
