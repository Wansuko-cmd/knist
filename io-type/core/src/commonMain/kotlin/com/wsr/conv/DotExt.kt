package com.wsr.conv

import com.wsr.BLAS

infix fun Array<DoubleArray>.dot(other: Array<DoubleArray>): Array<DoubleArray> {
    val result = Array(other.size) { DoubleArray(size) }
    for (f in other.indices) {
        for (i in indices) {
            result[f][i] = this[i] dot other[f]
        }
    }
    return result
}

infix fun DoubleArray.dot(other: DoubleArray): Double = BLAS.ddot(
    n = this.size,
    x = this,
    incX = 1,
    y = other,
    incY = 1,
)
