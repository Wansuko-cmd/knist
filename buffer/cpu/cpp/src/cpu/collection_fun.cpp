#include <collection_fun.h>

#define PERFORM_OPERATION(acc, x) \
    if constexpr (Op == Operation::Max) { \
        if (acc < x) { \
            acc = x; \
        } \
    } else if constexpr (Op == Operation::Min) { \
        if (acc > x) { \
            acc = x; \
        } \
    } else if constexpr (Op == Operation::Sum) { \
        acc += x; \
    }

enum class Operation {
    Max,
    Min,
    Sum
};

template<Operation Op>
inline float reduce_d1(const float* x, size_t size) {
    float acc = x[0];
    for (size_t i = 1; i < size; i++) {
        PERFORM_OPERATION(acc, x[i])
    }
    return acc;
}

template<Operation Op>
inline void reduce_d2(const float* x, int xi, int xj, int axis, float* result) {
    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            float acc = x[j];
            for (size_t i = 1; i < xi; i++) {
                PERFORM_OPERATION(acc, x[i * xj + j])
            }
            result[j] = acc;
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            float acc = x[i * xj];
            for (int j = 1; j < xj; j++) {
                PERFORM_OPERATION(acc, x[i * xj + j])
            }
            result[i] = acc;
        }
    }
}

template<Operation Op>
inline void reduce_d3(const float* x, int xi, int xj, int xk, int axis, float* result) {
    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                float acc = x[j * xk + k];
                for (int i = 1; i < xi; i++) {
                    PERFORM_OPERATION(acc, x[(i * xj + j) * xk + k])
                }
                result[j * xk + k] = acc;
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                float acc = x[i * xj * xk + k];
                for (int j = 1; j < xj; j++) {
                    PERFORM_OPERATION(acc, x[(i * xj + j) * xk + k])
                }
                result[i * xk + k] = acc;
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float acc = x[(i * xj + j) * xk];
                for (int k = 1; k < xk; k++) {
                    PERFORM_OPERATION(acc, x[(i * xj + j) * xk + k])
                }
                result[i * xj + j] = acc;
            }
        }
    }
}

template<Operation Op>
inline void reduce_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result) {
    if (axis == 0) {
        for (int j = 0; j < xj; j++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x[(j * xk + k) * xl + l];
                    for (int i = 1; i < xi; i++) {
                        PERFORM_OPERATION(acc, x[((i * xj + j) * xk + k) * xl + l])
                    }
                    result[(j * xk + k) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int k = 0; k < xk; k++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x[(i * xj * xk + k) * xl + l];
                    for (int j = 1; j < xj; j++) {
                        PERFORM_OPERATION(acc, x[((i * xj + j) * xk + k) * xl + l])
                    }
                    result[(i * xk + k) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int l = 0; l < xl; l++) {
                    float acc = x[(i * xj + j) * xk * xl + l];
                    for (int k = 1; k < xk; k++) {
                        PERFORM_OPERATION(acc, x[((i * xj + j) * xk + k) * xl + l])
                    }
                    result[(i * xj + j) * xl + l] = acc;
                }
            }
        }
    } else if (axis == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float acc = x[((i * xj + j) * xk + k) * xl];
                    for (int l = 1; l < xl; l++) {
                        PERFORM_OPERATION(acc, x[((i * xj + j) * xk + k) * xl + l])
                    }
                    result[(i * xj + j) * xk + k] = acc;
                }
            }
        }
    }
}

float average_d1(const float* x, size_t size) {
    return reduce_d1<Operation::Sum>(x, size) / size;
}

void average_d2(const float* x, int xi, int xj, int axis, float* result) {
    reduce_d2<Operation::Sum>(x, xi, xj, axis, result);
    int size;
    int n;
    if (axis == 0) {
        size = xj;
        n = xi;
    } else {
        size = xi;
        n = xj;
    }
    for (int i = 0; i < size; i++) {
        result[i] /= n;
    }
}

void average_d3(const float* x, int xi, int xj, int xk, int axis, float* result) {
    reduce_d3<Operation::Sum>(x, xi, xj, xk, axis, result);
    int size;
    int n;
    if (axis == 0) {
        size = xj * xk;
        n = xi;
    } else if (axis == 1) {
        size = xi * xk;
        n = xj;
    } else {
        size = xi * xj;
        n = xk;
    }
    for (int i = 0; i < size; i++) {
        result[i] /= n;
    }
}

void average_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result) {
    reduce_d4<Operation::Sum>(x, xi, xj, xk, xl, axis, result);
    int size;
    int n;
    if (axis == 0) {
        size = xj * xk * xl;
        n = xi;
    } else if (axis == 1) {
        size = xi * xk * xl;
        n = xj;
    } else if (axis == 2) {
        size = xi * xj * xl;
        n = xk;
    } else {
        size = xi * xj * xk;
        n = xl;
    }
    for (int i = 0; i < size; i++) {
        result[i] /= n;
    }
}

float max_d1(const float* x, size_t size) {
    return reduce_d1<Operation::Max>(x, size);
}

void max_d2(const float* x, int xi, int xj, int axis, float* result) {
    reduce_d2<Operation::Max>(x, xi, xj, axis, result);
}

void max_d3(const float* x, int xi, int xj, int xk, int axis, float* result) {
    reduce_d3<Operation::Max>(x, xi, xj, xk, axis, result);
}

void max_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result) {
    reduce_d4<Operation::Max>(x, xi, xj, xk, xl, axis, result);
}

float min_d1(const float* x, size_t size) {
    return reduce_d1<Operation::Min>(x, size);
}

void min_d2(const float* x, int xi, int xj, int axis, float* result) {
    reduce_d2<Operation::Min>(x, xi, xj, axis, result);
}

void min_d3(const float* x, int xi, int xj, int xk, int axis, float* result) {
    reduce_d3<Operation::Min>(x, xi, xj, xk, axis, result);
}

void min_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result) {
    reduce_d4<Operation::Min>(x, xi, xj, xk, xl, axis, result);
}

float sum_d1(const float* x, size_t size) {
    return reduce_d1<Operation::Sum>(x, size);
}

void sum_d2(const float* x, int xi, int xj, int axis, float* result) {
    reduce_d2<Operation::Sum>(x, xi, xj, axis, result);
}

void sum_d3(const float* x, int xi, int xj, int xk, int axis, float* result) {
    reduce_d3<Operation::Sum>(x, xi, xj, xk, axis, result);
}

void sum_d4(const float* x, int xi, int xj, int xk, int xl, int axis, float* result) {
    reduce_d4<Operation::Sum>(x, xi, xj, xk, xl, axis, result);
}
