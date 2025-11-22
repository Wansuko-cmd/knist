package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

operator fun Float.minus(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] = this - result[i]
    return IOType.d1(result)
}

operator fun Float.minus(other: IOType.D2): IOType.D2 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] = this - result[i]
    return IOType.d2(other.shape, result)
}

operator fun Float.minus(other: IOType.D3): IOType.D3 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] = this - result[i]
    return IOType.d3(other.shape, result)
}

operator fun IOType.D0.minus(other: IOType.D0) = IOType.d0(get() - other.get())

/**
 * IOType.D1
 */
operator fun IOType.D1.minus(other: Float): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] -= other
    return IOType.d1(result)
}

operator fun IOType.D1.minus(other: IOType.D1): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.saxpy(n = result.size, alpha = -1f, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d1(result)
}

/**
 * IOType.D2
 */
operator fun IOType.D2.minus(other: Float): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] -= other
    return IOType.d2(shape = shape, value = result)
}

operator fun IOType.D2.minus(other: IOType.D2): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.saxpy(n = result.size, alpha = -1f, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d2(this.shape, result)
}

operator fun IOType.D2.minus(other: IOType.D1): IOType.D2 {
    val result = this.value.copyOf()
    val cols = shape[1]
    for (row in 0 until shape[0]) {
        val offset = row * cols
        val subtrahend = other[row]
        for (col in 0 until cols) {
            result[offset + col] -= subtrahend
        }
    }
    return IOType.d2(this.shape, result)
}

/**
 * IOType.D3
 */
operator fun IOType.D3.minus(other: Float): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] -= other
    return IOType.d3(shape = shape, value = result)
}

operator fun IOType.D3.minus(other: IOType.D3): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.saxpy(n = result.size, alpha = -1f, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d3(this.shape, result)
}
