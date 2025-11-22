package com.wsr.batch.average

import com.wsr.BLAS
import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.average
import com.wsr.get
import com.wsr.operator.div
import com.wsr.operator.plus

@JvmName("batchD2sAverageBatch")
fun Batch<IOType.D2>.average(): Batch<IOType.D0> = Batch(size) { IOType.d0(this[it].average()) }

@JvmName("batchD2sAverageWithAxis")
fun Batch<IOType.D2>.average(axis: Int) = Batch(size) { this[it].average(axis) }

@JvmName("batchD2sBatchAverage")
fun Batch<IOType.D2>.batchAverage(): IOType.D2 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
