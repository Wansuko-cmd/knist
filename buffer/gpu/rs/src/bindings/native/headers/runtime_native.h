#ifndef RUNTIME_NATIVE_H
#define RUNTIME_NATIVE_H

#include <stdbool.h>
#include "buffer_native.h"

Runtime* com_wsr_gpu_runtime_allocate(long long pool_size, bool enable_profiler);
void com_wsr_gpu_runtime_release(Runtime* runtime);
void com_wsr_gpu_runtime_flush(const Runtime* runtime);
void com_wsr_gpu_runtime_sync(const Runtime* runtime);

#endif
