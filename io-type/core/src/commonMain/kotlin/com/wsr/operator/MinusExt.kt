package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

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

@JvmName("minusD1sToFloat")
operator fun List<IOType.D1>.minus(other: Float) = map { it - other }

@JvmName("minusD1sToFloats")
operator fun List<IOType.D1>.minus(other: List<Float>) = List(size) { this[it] - other[it] }

@JvmName("minusD1sToD1")
operator fun List<IOType.D1>.minus(other: IOType.D1) = List(size) { this[it] - other }

@JvmName("minusD1sToD1s")
operator fun List<IOType.D1>.minus(other: List<IOType.D1>) = List(size) { this[it] - other[it] }

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

@JvmName("minusD2sToFloats")
operator fun List<IOType.D2>.minus(other: List<Float>) = List(size) { this[it] - other[it] }

@JvmName("minusD2sToD2")
operator fun List<IOType.D2>.minus(other: IOType.D2) = List(size) { this[it] - other }

@JvmName("minusD2sToD2s")
operator fun List<IOType.D2>.minus(other: List<IOType.D2>) = List(size) { this[it] - other[it] }

@JvmName("minusD2sToD1s")
operator fun List<IOType.D2>.minus(other: List<IOType.D1>) = List(size) { this[it] - other[it] }

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

@JvmName("minusD3sToFloats")
operator fun List<IOType.D3>.minus(other: List<Float>) = List(size) { this[it] - other[it] }

@JvmName("minusD3sToD3")
operator fun List<IOType.D3>.minus(other: IOType.D3) = List(size) { this[it] - other }

@JvmName("minusD3sToD3s")
operator fun List<IOType.D3>.minus(other: List<IOType.D3>) = List(size) { this[it] - other[it] }
