#ifndef MATH_FUN_H
#define MATH_FUN_H

#include <cstddef>

#ifdef __cplusplus
extern "C" {
#endif

void exp_d1(const float* x, float* result, size_t size);

void ln_d1(const float* x, float e, float* result, size_t size);

void pow_d1(const float* x, int n, float* result, size_t size);

void sqrt_d1(const float* x, float e, float* result, size_t size);

#ifdef __cplusplus
}
#endif
#endif
