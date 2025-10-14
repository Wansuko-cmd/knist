package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * IOType.D1
 */
operator fun IOType.D1.minus(other: Double): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] -= other
    return IOType.d1(result)
}

operator fun IOType.D1.minus(other: IOType.D1): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = -1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d1(result)
}

@JvmName("minusD1sToDouble")
operator fun List<IOType.D1>.minus(other: Double) = map { it - other }

@JvmName("minusD1sToDoubles")
operator fun List<IOType.D1>.minus(other: List<Double>) = List(size) { this[it] - other[it] }

@JvmName("minusD1sToD1")
operator fun List<IOType.D1>.minus(other: IOType.D1) = List(size) { this[it] - other }

@JvmName("minusD1sToD1s")
operator fun List<IOType.D1>.minus(other: List<IOType.D1>) = List(size) { this[it] - other[it] }

/**
 * IOType.D2
 */
operator fun IOType.D2.minus(other: Double): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] -= other
    return IOType.d2(shape = shape, value = result)
}

operator fun IOType.D2.minus(other: IOType.D2): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = -1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d2(this.shape, result)
}

@JvmName("minusD2sToDoubles")
operator fun List<IOType.D2>.minus(other: List<Double>) = List(size) { this[it] - other[it] }

@JvmName("minusD2sToD2")
operator fun List<IOType.D2>.minus(other: IOType.D2) = List(size) { this[it] - other }

@JvmName("minusD2sToD2s")
operator fun List<IOType.D2>.minus(other: List<IOType.D2>) = List(size) { this[it] - other[it] }

/**
 * IOType.D3
 */
operator fun IOType.D3.minus(other: Double): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] -= other
    return IOType.d3(shape = shape, value = result)
}

operator fun IOType.D3.minus(other: IOType.D3): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = -1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d3(this.shape, result)
}

@JvmName("minusD3sToDoubles")
operator fun List<IOType.D3>.minus(other: List<Double>) = List(size) { this[it] - other[it] }

@JvmName("minusD3sToD3")
operator fun List<IOType.D3>.minus(other: IOType.D3) = List(size) { this[it] - other }

@JvmName("minusD3sToD3s")
operator fun List<IOType.D3>.minus(other: List<IOType.D3>) = List(size) { this[it] - other[it] }
