package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

operator fun Double.plus(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    for (i in result.indices) {
        result[i] += this
    }
    return IOType.d1(result)
}

@JvmName("plusToD1s")
operator fun List<Double>.plus(other: List<IOType.D1>): List<IOType.D1> = List(size) { this[it] + other[it] }

operator fun IOType.D1.plus(other: Double): IOType.D1 = other.plus(this)

@JvmName("plusToDoubles")
operator fun List<IOType.D1>.plus(other: List<Double>): List<IOType.D1> = other.plus(this)

operator fun IOType.D1.plus(other: IOType.D1): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = 1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d1(result)
}

operator fun List<IOType.D1>.plus(other: IOType.D1) = List(size) { this[it] + other }

operator fun List<IOType.D1>.plus(other: List<IOType.D1>) = List(size) { this[it] + other[it] }

operator fun IOType.D2.plus(other: IOType.D2): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = 1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d2(this.shape, result)
}

operator fun List<IOType.D2>.plus(other: IOType.D2) = List(size) { this[it] + other }

operator fun IOType.D3.plus(other: IOType.D3): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = 1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d3(this.shape, result)
}

operator fun List<IOType.D3>.plus(other: IOType.D3) = List(size) { this[it] + other }
