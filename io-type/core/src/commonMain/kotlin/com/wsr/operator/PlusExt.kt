package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Float
 */
operator fun Float.plus(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] += this
    return IOType.d1(result)
}

operator fun Float.plus(other: IOType.D2): IOType.D2 = other + this

operator fun Float.plus(other: IOType.D3): IOType.D3 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] += this
    return IOType.d3(shape = other.shape, value = result)
}

/**
 * IOType.D1
 */
operator fun IOType.D1.plus(other: Float): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] += other
    return IOType.d1(result)
}

operator fun IOType.D1.plus(other: IOType.D1): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.saxpy(n = result.size, alpha = 1f, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d1(result)
}

/**
 * IOType.D2
 */
operator fun IOType.D2.plus(other: Float): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] += other
    return IOType.d2(shape, result)
}

operator fun IOType.D2.plus(other: IOType.D2): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.saxpy(n = result.size, alpha = 1f, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d2(this.shape, result)
}

/**
 * IOType.D3
 */
operator fun IOType.D3.plus(other: Float): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] += other
    return IOType.d3(shape, result)
}

operator fun IOType.D3.plus(other: IOType.D3): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.saxpy(n = result.size, alpha = 1f, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d3(this.shape, result)
}
