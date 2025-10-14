package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Double
 */
operator fun Double.times(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    BLAS.dscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d1(result)
}

@JvmName("timesDoubleToD1s")
operator fun Double.times(other: List<IOType.D1>) = other.map { this * it }

operator fun Double.times(other: IOType.D2): IOType.D2 {
    val result = other.value.copyOf()
    BLAS.dscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d2(other.shape, result)
}

@JvmName("timesDoubleToD2s")
operator fun Double.times(other: List<IOType.D2>) = other.map { this * it }

operator fun Double.times(other: IOType.D3): IOType.D3 {
    val result = other.value.copyOf()
    BLAS.dscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d3(other.shape, result)
}

@JvmName("timesDoubleToD3s")
operator fun Double.times(other: List<IOType.D3>) = other.map { this * it }

/**
 * IOType.D1
 */
operator fun IOType.D1.times(other: IOType.D1): IOType.D1 {
    val result = value.copyOf()
    for (i in result.indices) {
        result[i] *= other.value[i]
    }
    return IOType.d1(result)
}

operator fun IOType.D1.times(other: List<IOType.D1>) = other.map { this * it }

operator fun List<IOType.D1>.times(other: IOType.D1) = map { it * other }

/**
 * IOType.D2
 */
operator fun IOType.D2.times(other: IOType.D2): IOType.D2 {
    val result = value.copyOf()
    for (i in result.indices) {
        result[i] *= other.value[i]
    }
    return IOType.d2(shape, result)
}

operator fun IOType.D2.times(other: List<IOType.D2>) = other.map { this * it }

/**
 * IOType.D3
 */
operator fun IOType.D3.times(other: IOType.D3): IOType.D3 {
    val result = value.copyOf()
    for (i in result.indices) {
        result[i] *= other.value[i]
    }
    return IOType.d3(shape, result)
}

operator fun IOType.D3.times(other: List<IOType.D3>) = other.map { this * it }
