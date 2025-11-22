package com.wsr.batch.average

import com.wsr.BLAS
import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get

@JvmName("batchD3sAverageBatch")
fun Batch<IOType.D3>.average(): Batch<IOType.D0> = Batch(size) {
    val value = this[it].value
    var sum = 0f
    for (element in value) sum += element
    IOType.d0(sum / value.size)
}

@JvmName("batchD3sBatchAverage")
fun Batch<IOType.D3>.batchAverage(): IOType.D3 {
    val result = value.sliceArray(0 until step)
    for (i in 1 until size) {
        BLAS.saxpy(n = result.size, alpha = 1f, x = this[i].value, incX = 1, y = result, incY = 1)
    }
    BLAS.sscal(n = result.size, alpha = 1f / size, x = result, incX = 1)
    return IOType.d3(shape, result)
}
