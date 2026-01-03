#ifndef COLLECTION_FUN_H
#define COLLECTION_FUN_H

#include <cstddef>

#ifdef __cplusplus
extern "C" {
#endif

float average_d1(const float* x, size_t size);

void average_d2(const float* x, int xi, int xj, int axis, float* result);

void average_d3(const float* x, int xi, int xj, int xk, int axis, float* result);

void average_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result);

float max_d1(const float* x, size_t size);

void max_d2(const float* x, int xi, int xj, int axis, float* result);

void max_d3(const float* x, int xi, int xj, int xk, int axis, float* result);

void max_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result);

float min_d1(const float* x, size_t size);

void min_d2(const float* x, int xi, int xj, int axis, float* result);

void min_d3(const float* x, int xi, int xj, int xk, int axis, float* result);

void min_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result);

float sum_d1(const float* x, size_t size);

void sum_d2(const float* x, int xi, int xj, int axis, float* result);

void sum_d3(const float* x, int xi, int xj, int xk, int axis, float* result);

void sum_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result);

#ifdef __cplusplus
}
#endif
#endif
