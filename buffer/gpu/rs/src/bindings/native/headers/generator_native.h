#ifndef GENERATOR_NATIVE_H
#define GENERATOR_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

void com_wsr_gpu_random(float from, float until, long long seed, const GPUBuffer* result, Runtime* runtime);

#endif
