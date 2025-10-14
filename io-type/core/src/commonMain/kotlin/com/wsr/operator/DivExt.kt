package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Double
 */
operator fun Double.div(other: IOType.D1) = IOType.d1(other.shape) { i -> this / other[i] }

operator fun Double.div(other: IOType.D2) = IOType.d2(other.shape) { i, j -> this / other[i, j] }

operator fun Double.div(other: IOType.D3) = IOType.d3(other.shape) { i, j, k -> this / other[i, j, k] }

/**
 * IOType.D1
 */
operator fun IOType.D1.div(other: Double): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.dscal(n = result.size, alpha = 1.0 / other, x = result, incX = 1)
    return IOType.d1(result)
}

@JvmName("divD1sToDoubles")
operator fun List<IOType.D1>.div(other: List<Double>) = List(size) { this[it] / other[it] }

/**
 * IOType.D2
 */
operator fun IOType.D2.div(other: Double): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.dscal(n = result.size, alpha = 1.0 / other, x = result, incX = 1)
    return IOType.d2(shape, result)
}

@JvmName("divD2sToDouble")
operator fun List<IOType.D2>.div(other: Double) = map { it / other }

@JvmName("divD2sToDoubles")
operator fun List<IOType.D2>.div(other: List<Double>) = List(size) { this[it] / other[it] }

/**
 * IOType.D3
 */
operator fun IOType.D3.div(other: Double): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.dscal(n = result.size, alpha = 1.0 / other, x = result, incX = 1)
    return IOType.d3(shape, result)
}

@JvmName("divD3sToDouble")
operator fun List<IOType.D3>.div(other: Double) = map { it / other }

@JvmName("divD3sToDoubles")
operator fun List<IOType.D3>.div(other: List<Double>) = List(size) { this[it] / other[it] }
