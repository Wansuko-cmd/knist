package com.wsr.dot

import com.wsr.BLAS
import com.wsr.IOType

infix fun IOType.D1.dot(other: IOType.D1): Double = BLAS.ddot(
    n = value.size,
    x = value,
    incX = 1,
    y = other.value,
    incY = 1,
)

infix fun IOType.D1.dot(other: IOType.D2) = IOType.d1(size = shape[0]) { i ->
    var sum = 0.0
    for (j in 0 until other.shape[1]) {
        sum += other[i, j]
    }
    this[i] * sum
}

@JvmName("dotToD1s")
infix fun IOType.D1.dot(other: List<IOType.D1>) = List(other.size) { this dot other[it] }

@JvmName("dotToD2s")
infix fun IOType.D1.dot(other: List<IOType.D2>) = List(other.size) { this dot other[it] }

@JvmName("dotToD1s")
infix fun List<IOType.D1>.dot(other: List<IOType.D1>) = List(size) { this[it] dot other[it] }

@JvmName("dotToD2s")
infix fun List<IOType.D1>.dot(other: List<IOType.D2>) = List(size) { this[it] dot other[it] }
