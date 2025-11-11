package com.wsr.collection

import com.wsr.BLAS
import com.wsr.IOType

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
