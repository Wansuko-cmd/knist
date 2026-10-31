#ifndef INDEX_NATIVE_H
#define INDEX_NATIVE_H

#include "buffer_native.h"

#ifdef __cplusplus
extern "C" {
#endif

void com_wsr_cpu_gather(const CPUBuffer* x, const CPUBuffer* y, int i, int j, int k, CPUBuffer* result);

void com_wsr_cpu_scatter_add(const CPUBuffer* x, const CPUBuffer* y, int i, int j, int k, int b, CPUBuffer* result);

#ifdef __cplusplus
}
#endif
#endif
