package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Double
 */
operator fun Double.plus(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] += this
    return IOType.d1(result)
}

operator fun Double.plus(other: IOType.D2): IOType.D2 = other + this

operator fun Double.plus(other: List<IOType.D2>): List<IOType.D2> = other.map { this + it }

operator fun Double.plus(other: IOType.D3): IOType.D3 {
    val result = other.value.copyOf()
    for (i in result.indices) result[i] += this
    return IOType.d3(shape = other.shape, value =result)
}

@JvmName("plusToD1s")
operator fun List<Double>.plus(other: List<IOType.D1>): List<IOType.D1> = List(size) { this[it] + other[it] }

@JvmName("plusDoublesToD2s")
operator fun List<Double>.plus(other: List<IOType.D2>) = List(size) { this[it] + other[it] }

@JvmName("plusDoublesToD3s")
operator fun List<Double>.plus(other: List<IOType.D3>) = List(size) { this[it] + other[it] }

/**
 * IOType.D1
 */
operator fun IOType.D1.plus(other: Double): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] += other
    return IOType.d1(result)
}

operator fun IOType.D1.plus(other: IOType.D1): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = 1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d1(result)
}

@JvmName("plusToDoubles")
operator fun List<IOType.D1>.plus(other: List<Double>): List<IOType.D1> = other.plus(this)

operator fun List<IOType.D1>.plus(other: IOType.D1) = List(size) { this[it] + other }

@JvmName("plusToD1List")
operator fun List<IOType.D1>.plus(other: List<IOType.D1>) = List(size) { this[it] + other[it] }

/**
 * IOType.D2
 */
operator fun IOType.D2.plus(other: Double): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] += other
    return IOType.d2(shape, result)
}

operator fun IOType.D2.plus(other: IOType.D2): IOType.D2 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = 1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d2(this.shape, result)
}

operator fun List<IOType.D2>.plus(other: Double): List<IOType.D2> = map { it + other }

@JvmName("plusD2ListToDoubles")
operator fun List<IOType.D2>.plus(other: List<Double>) = List(size) { this[it] + other[it] }

operator fun List<IOType.D2>.plus(other: IOType.D2) = List(size) { this[it] + other }

@JvmName("plusD2sToD2List")
operator fun List<IOType.D2>.plus(other: List<IOType.D2>) = List(size) { this[it] + other[it] }

/**
 * IOType.D3
 */
operator fun IOType.D3.plus(other: Double): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] += other
    return IOType.d3(shape, result)
}

operator fun IOType.D3.plus(other: IOType.D3): IOType.D3 {
    val result = this.value.copyOf()
    BLAS.daxpy(n = result.size, alpha = 1.0, x = other.value, incX = 1, y = result, incY = 1)
    return IOType.d3(this.shape, result)
}

@JvmName("plusToDoubles")
operator fun List<IOType.D3>.plus(other: List<Double>): List<IOType.D3> = other.plus(this)

operator fun List<IOType.D3>.plus(other: IOType.D3) = List(size) { this[it] + other }

@JvmName("plusToD1List")
operator fun List<IOType.D3>.plus(other: List<IOType.D3>) = List(size) { this[it] + other[it] }
