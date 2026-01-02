#include <operation_fun.h>

#define PERFORM_OPERATION(result, x, y) \
    if constexpr (Op == Operation::Plus) { \
        result = x + y; \
    } else if constexpr (Op == Operation::Minus) { \
        result = x - y; \
    } else if constexpr (Op == Operation::Times) { \
        result = x * y; \
    } else if constexpr (Op == Operation::Div) { \
        result = x / y; \
    }

enum class Operation {
    Plus,
    Minus,
    Times,
    Div
};

template<Operation Op>
inline void zip_with_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
) {
    for (size_t i = 0; i < y_size; i++) {
        PERFORM_OPERATION(result[i], x, y[i]);
    }
}

template<Operation Op>
inline void zip_with_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
) {
    for (size_t i = 0; i < x_size; i++) {
        PERFORM_OPERATION(result[i], x[i], y);
    }
}

template<Operation Op>
inline void zip_with_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
) {
    for (size_t i = 0; i < size; i++) {
        PERFORM_OPERATION(result[i], x[i], y[i]);
    }
}

template<Operation Op>
inline void zip_with_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
) {
    if (axis == 0) {
        for (int i = 0; i < yi; i++) {
            float x_value = x[i];
            int y_i = i * yj;
            for (int j = 0; j < yj; j++) {
                int index = y_i + j;
                PERFORM_OPERATION(result[index], x_value, y[index]);
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < yi; i++) {
            int y_i = i * yj;
            for (int j = 0; j < yj; j++) {
                int index = y_i + j;
                PERFORM_OPERATION(result[index], x[j], y[index]);
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
) {
    if (axis == 0) {
        for (int i = 0; i < yi; i++) {
            float x_value = x[i];
            for (int j = 0; j < yj; j++) {
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int index = y_i + k;
                    PERFORM_OPERATION(result[index], x_value, y[index]);
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x[j];
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int index = y_i + k;
                    PERFORM_OPERATION(result[index], x_value, y[index]);
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int index = y_i + k;
                    PERFORM_OPERATION(result[index], x[k], y[index]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
) {
    if (axis == 0) {
        for (int i = 0; i < xi; i++) {
            float y_value = y[i];
            int x_i = i * xj;
            for (int j = 0; j < xj; j++) {
                int index = x_i + j;
                PERFORM_OPERATION(result[index], x[index], y_value);
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            int x_i = i * xj;
            for (int j = 0; j < xj; j++) {
                int index = x_i + j;
                PERFORM_OPERATION(result[index], x[index], y[j]);
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
) {
    if (axis1 == 0 && axis2 == 1) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                float x_value = x[i * yj + j];
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int y_index = y_i + k;
                    PERFORM_OPERATION(result[y_index], x_value, y[y_index]);
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int x_i = i * yk;
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int x_index = x_i + k;
                    int y_index = y_i + k;
                    PERFORM_OPERATION(result[y_index], x[x_index], y[y_index]);
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                int x_i = j * yk;
                int y_i = (i * yj + j) * yk;
                for (int k = 0; k < yk; k++) {
                    int x_index = x_i + k;
                    int y_index = y_i + k;
                    PERFORM_OPERATION(result[y_index], x[x_index], y[y_index]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
) {
    if (axis == 0) {
        for (int i = 0; i < xi; i++) {
            float y_value = y[i];
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result[index], x[index], y_value);
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y[j];
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result[index], x[index], y_value);
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result[index], x[index], y[k]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    if (axis1 == 0 && axis2 == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                float y_value = y[i * yj + j];
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    PERFORM_OPERATION(result[index], x[index], y_value);
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    int y_index = i * yj + k;
                    PERFORM_OPERATION(result[index], x[index], y[y_index]);
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                int x_i = (i * xj + j) * xk;
                for (int k = 0; k < xk; k++) {
                    int index = x_i + k;
                    int y_index = j * yj + k;
                    PERFORM_OPERATION(result[index], x[index], y[y_index]);
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
) {
    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    float x_value = x[(i * xj + j) * xk + k];
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[y_index], x_value, y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int x_index = (i * xj + j) * xk + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[y_index], x[x_index], y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int x_index = (i * xj + k) * xk + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[y_index], x[x_index], y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < yi; i++) {
            for (int j = 0; j < yj; j++) {
                for (int k = 0; k < yk; k++) {
                    int y_i = ((i * yj + j) * yk + k) * yl;
                    for (int l = 0; l < yl; l++) {
                        int x_index = (j * xj + k) * xk + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[y_index], x[x_index], y[y_index]);
                    }
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
) {
    if (axis == 0) {
        for (int i = 0; i < xi; i++) {
            float y_value = y[i];
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y[j];
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y[k];
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y[l]);
                    }
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    if (axis1 == 0 && axis2 == 1) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                float y_value = y[i * yj + j];
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y[i * yj + k];
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = i * yj + l;
                        PERFORM_OPERATION(result[index], x[index], y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    float y_value = y[j * yj + k];
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = j * yj + l;
                        PERFORM_OPERATION(result[index], x[index], y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 2 && axis2 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = k * yj + l;
                        PERFORM_OPERATION(result[index], x[index], y[y_index]);
                    }
                }
            }
        }
    }
}

template<Operation Op>
inline void zip_with_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
) {
    if (axis1 == 0 && axis2 == 1 && axis3 == 2) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    float y_value = y[(i * yj + j) * yk + k];
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        PERFORM_OPERATION(result[index], x[index], y_value);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 1 && axis3 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    int y_i = (i * yj + j) * yk;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[index], x[index], y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 0 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    int y_i = (i * yj + k) * yk;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[index], x[index], y[y_index]);
                    }
                }
            }
        }
    } else if (axis1 == 1 && axis2 == 2 && axis3 == 3) {
        for (int i = 0; i < xi; i++) {
            for (int j = 0; j < xj; j++) {
                for (int k = 0; k < xk; k++) {
                    int x_i = ((i * xj + j) * xk + k) * xl;
                    int y_i = (j * yj + k) * yk;
                    for (int l = 0; l < xl; l++) {
                        int index = x_i + l;
                        int y_index = y_i + l;
                        PERFORM_OPERATION(result[index], x[index], y[y_index]);
                    }
                }
            }
        }
    }
}

void plus_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
) {
    zip_with_d0_to_d1<Operation::Plus>(x, y, y_size, result);
}

void plus_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
) {
    zip_with_d1_to_d0<Operation::Plus>(x, x_size, y, result);
}

void plus_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
) {
    zip_with_d1_to_d1<Operation::Plus>(x, y, size, result);
}

void plus_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
) {
    zip_with_d1_to_d2<Operation::Plus>(x, y, yi, yj, axis, result);
}

void plus_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
) {
    zip_with_d1_to_d3<Operation::Plus>(x, y, yi, yj, yk, axis, result);
}

void plus_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d2_to_d1<Operation::Plus>(x, xi, xj, y, axis, result);
}

void plus_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
) {
    zip_with_d2_to_d3<Operation::Plus>(x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

void plus_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d3_to_d1<Operation::Plus>(x, xi, xj, xk, y, axis, result);
}

void plus_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d3_to_d2<Operation::Plus>(x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

void plus_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d3_to_d4<Operation::Plus>(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

void plus_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d4_to_d1<Operation::Plus>(x, xi, xj, xk, xl, y, axis, result);
}

void plus_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d4_to_d2<Operation::Plus>(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

void plus_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d4_to_d3<Operation::Plus>(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}

void minus_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
) {
    zip_with_d0_to_d1<Operation::Minus>(x, y, y_size, result);
}

void minus_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
) {
    zip_with_d1_to_d0<Operation::Minus>(x, x_size, y, result);
}

void minus_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
) {
    zip_with_d1_to_d1<Operation::Minus>(x, y, size, result);
}

void minus_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
) {
    zip_with_d1_to_d2<Operation::Minus>(x, y, yi, yj, axis, result);
}

void minus_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
) {
    zip_with_d1_to_d3<Operation::Minus>(x, y, yi, yj, yk, axis, result);
}

void minus_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d2_to_d1<Operation::Minus>(x, xi, xj, y, axis, result);
}

void minus_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
) {
    zip_with_d2_to_d3<Operation::Minus>(x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

void minus_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d3_to_d1<Operation::Minus>(x, xi, xj, xk, y, axis, result);
}

void minus_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d3_to_d2<Operation::Minus>(x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

void minus_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d3_to_d4<Operation::Minus>(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

void minus_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d4_to_d1<Operation::Minus>(x, xi, xj, xk, xl, y, axis, result);
}

void minus_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d4_to_d2<Operation::Minus>(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

void minus_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d4_to_d3<Operation::Minus>(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}

void times_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
) {
    zip_with_d0_to_d1<Operation::Times>(x, y, y_size, result);
}

void times_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
) {
    zip_with_d1_to_d0<Operation::Times>(x, x_size, y, result);
}

void times_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
) {
    zip_with_d1_to_d1<Operation::Times>(x, y, size, result);
}

void times_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
) {
    zip_with_d1_to_d2<Operation::Times>(x, y, yi, yj, axis, result);
}

void times_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
) {
    zip_with_d1_to_d3<Operation::Times>(x, y, yi, yj, yk, axis, result);
}

void times_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d2_to_d1<Operation::Times>(x, xi, xj, y, axis, result);
}

void times_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
) {
    zip_with_d2_to_d3<Operation::Times>(x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

void times_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d3_to_d1<Operation::Times>(x, xi, xj, xk, y, axis, result);
}

void times_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d3_to_d2<Operation::Times>(x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

void times_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d3_to_d4<Operation::Times>(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

void times_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d4_to_d1<Operation::Times>(x, xi, xj, xk, xl, y, axis, result);
}

void times_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d4_to_d2<Operation::Times>(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

void times_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d4_to_d3<Operation::Times>(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}

void div_d0_to_d1(
    float x,
    const float* y,
    size_t y_size,
    float* result
) {
    zip_with_d0_to_d1<Operation::Div>(x, y, y_size, result);
}

void div_d1_to_d0(
    const float* x,
    size_t x_size,
    float y,
    float* result
) {
    zip_with_d1_to_d0<Operation::Div>(x, x_size, y, result);
}

void div_d1_to_d1(
    const float* x,
    const float* y,
    size_t size,
    float* result
) {
    zip_with_d1_to_d1<Operation::Div>(x, y, size, result);
}

void div_d1_to_d2(
    const float* x,
    const float* y, int yi, int yj,
    int axis,
    float* result
) {
    zip_with_d1_to_d2<Operation::Div>(x, y, yi, yj, axis, result);
}

void div_d1_to_d3(
    const float* x,
    const float* y, int yi, int yj, int yk,
    int axis,
    float* result
) {
    zip_with_d1_to_d3<Operation::Div>(x, y, yi, yj, yk, axis, result);
}

void div_d2_to_d1(
    const float* x, int xi, int xj,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d2_to_d1<Operation::Div>(x, xi, xj, y, axis, result);
}

void div_d2_to_d3(
    const float* x, int xi, int xj,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2,
    float* result
) {
    zip_with_d2_to_d3<Operation::Div>(x, xi, xj, y, yi, yj, yk, axis1, axis2, result);
}

void div_d3_to_d1(
    const float* x, int xi, int xj, int xk,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d3_to_d1<Operation::Div>(x, xi, xj, xk, y, axis, result);
}

void div_d3_to_d2(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d3_to_d2<Operation::Div>(x, xi, xj, xk, y, yi, yj, axis1, axis2, result);
}

void div_d3_to_d4(
    const float* x, int xi, int xj, int xk,
    const float* y, int yi, int yj, int yk, int yl,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d3_to_d4<Operation::Div>(x, xi, xj, xk, y, yi, yj, yk, yl, axis1, axis2, axis3, result);
}

void div_d4_to_d1(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y,
    int axis,
    float* result
) {
    zip_with_d4_to_d1<Operation::Div>(x, xi, xj, xk, xl, y, axis, result);
}

void div_d4_to_d2(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj,
    int axis1, int axis2,
    float* result
) {
    zip_with_d4_to_d2<Operation::Div>(x, xi, xj, xk, xl, y, yi, yj, axis1, axis2, result);
}

void div_d4_to_d3(
    const float* x, int xi, int xj, int xk, int xl,
    const float* y, int yi, int yj, int yk,
    int axis1, int axis2, int axis3,
    float* result
) {
    zip_with_d4_to_d3<Operation::Div>(x, xi, xj, xk, xl, y, yi, yj, yk, axis1, axis2, axis3, result);
}
