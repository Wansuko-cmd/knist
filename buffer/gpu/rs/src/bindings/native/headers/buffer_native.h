#ifndef BUFFER_NATIVE_H
#define BUFFER_NATIVE_H

#include <stdbool.h>

typedef struct GPUBuffer GPUBuffer;
typedef struct Runtime Runtime;

const GPUBuffer* com_wsr_gpu_buffer_allocate(int size, const Runtime* runtime);
const GPUBuffer* com_wsr_gpu_buffer_init(const float* value, int size, const Runtime* runtime);
void com_wsr_gpu_buffer_release(const GPUBuffer* ptr, const Runtime* runtime);
void com_wsr_gpu_buffer_read_all(const GPUBuffer* ptr, Runtime* runtime, float* result, int size);
void com_wsr_gpu_buffer_write(const GPUBuffer* ptr, int index, float value, Runtime* runtime);

#endif
