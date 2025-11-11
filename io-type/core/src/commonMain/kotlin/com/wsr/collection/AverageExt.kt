package com.wsr.collection

import com.wsr.BLAS
import com.wsr.IOType

fun IOType.D1.average(): Double = value.average()

@JvmName("averageToD1s")
fun List<IOType.D1>.average(): List<Double> = map { it.average() }

fun IOType.D2.average(): IOType.D1 = IOType.d1(shape[0]) { get(it).average() }

@JvmName("averageToD2s")
fun List<IOType.D2>.average(): List<IOType.D1> = map { it.average() }

fun IOType.D3.average(): IOType.D2 = IOType.d2(shape[0], shape[1]) { x, y -> get(x, y).average() }

@JvmName("averageToD3s")
fun List<IOType.D3>.average(): List<IOType.D2> = map { it.average() }

/**
 * batch average
 */
fun List<IOType.D1>.batchAverage(): IOType.D1 {
    val result = first().value.copyOf()
    for (i in 1 until size) {
        BLAS.daxpy(n = result.size, alpha = 1.0, x = this[i].value, incX = 1, y = result, incY = 1)
    }
    BLAS.dscal(n = result.size, alpha = 1.0 / size, x = result, incX = 1)
    return IOType.D1(result)
}

fun List<IOType.D2>.batchAverage(): IOType.D2 {
    val result = first().value.copyOf()
    for (i in 1 until size) {
        BLAS.daxpy(n = result.size, alpha = 1.0, x = this[i].value, incX = 1, y = result, incY = 1)
    }
    BLAS.dscal(n = result.size, alpha = 1.0 / size, x = result, incX = 1)
    return IOType.D2(result, first().shape)
}

fun List<IOType.D3>.batchAverage(): IOType.D3 {
    val result = first().value.copyOf()
    for (i in 1 until size) {
        BLAS.daxpy(n = result.size, alpha = 1.0, x = this[i].value, incX = 1, y = result, incY = 1)
    }
    BLAS.dscal(n = result.size, alpha = 1.0 / size, x = result, incX = 1)
    return IOType.D3(result, first().shape)
}
