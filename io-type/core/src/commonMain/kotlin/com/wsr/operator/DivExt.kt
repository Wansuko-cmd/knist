package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Float
 */
operator fun Float.div(other: IOType.D0) = IOType.d0(this / other.get())

operator fun Float.div(other: IOType.D1) = IOType.d1(other.shape) { i -> this / other[i] }

operator fun Float.div(other: IOType.D2) = IOType.d2(other.shape) { i, j -> this / other[i, j] }

operator fun Float.div(other: IOType.D3) = IOType.d3(other.shape) { i, j, k -> this / other[i, j, k] }

/**
 * IOType.D1
 */
operator fun IOType.D1.div(other: Float): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.sscal(n = result.size, alpha = 1f / other, x = result, incX = 1)
    return IOType.d1(result)
}

operator fun IOType.D1.div(other: IOType.D1): IOType.D1 = IOType.d1(this.shape) { i ->
    this[i] / other[i]
}

/**
 * IOType.D2
 */
operator fun IOType.D2.div(other: Float): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.sscal(n = result.size, alpha = 1f / other, x = result, incX = 1)
    return IOType.d2(shape, result)
}

/**
 * Broadcasting: D2 [rows, cols] / D1 [rows]
 * 各行を対応するD1の要素で割る（NumPyのbroadcasting互換）
 */
operator fun IOType.D2.div(other: IOType.D1): IOType.D2 {
    require(shape[0] == other.shape[0]) {
        "Broadcasting error: D2 shape[0]=${shape[0]} must equal D1 size=${other.shape[0]}"
    }
    val result = this.value.copyOf()
    val cols = shape[1]
    for (row in 0 until shape[0]) {
        val offset = row * cols
        val divisor = other[row]
        for (col in 0 until cols) {
            result[offset + col] /= divisor
        }
    }
    return IOType.d2(this.shape, result)
}

operator fun IOType.D2.div(other: IOType.D2): IOType.D2 = IOType.d2(this.shape) { i, j ->
    this[i, j] / other[i, j]
}

/**
 * IOType.D3
 */
operator fun IOType.D3.div(other: Float): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.sscal(n = result.size, alpha = 1f / other, x = result, incX = 1)
    return IOType.d3(shape, result)
}

operator fun IOType.D3.div(other: IOType.D3): IOType.D3 = IOType.d3(this.shape) { i, j, k ->
    this[i, j, k] / other[i, j, k]
}
