#ifndef PROFILER_NATIVE_H
#define PROFILER_NATIVE_H

#include "buffer_native.h"

long long com_wsr_gpu_profiler_take_cpu(const Runtime* runtime);
void com_wsr_gpu_profiler_take_gpu(const Runtime* runtime, long long* result);

#endif
