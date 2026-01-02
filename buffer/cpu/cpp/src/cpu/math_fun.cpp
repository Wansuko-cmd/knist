#include <math_fun.h>

#include <cmath>
#include <algorithm>

void exp_d1(const float* x, float* result, size_t size) {
    for (size_t i = 0; i < size; i++) {
        result[i] = std::exp(x[i]);
    }
}

void ln_d1(const float* x, float e, float* result, size_t size) {
    for (size_t i = 0; i < size; i++) {
        result[i] = std::log(x[i] + e);
    }
}

void pow_d1(const float* x, int n, float* result, size_t size) {
    for (size_t i = 0; i < size; i++) {
        result[i] = std::pow(x[i], n);
    }
}

void sqrt_d1(const float* x, float e, float* result, size_t size) {
    for (size_t i = 0; i < size; i++) {
        result[i] = std::sqrt(x[i] + e);
    }
}
