package com.wsr.batch.collecction.average

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.core.d0
import com.wsr.core.operation.div
import com.wsr.core.operation.plus
@JvmName("batchD3sAverageBatch")
fun Batch<IOType.D3>.average(): Batch<IOType.D0> = Batch(size) { IOType.d0(this[it].average()) }

@JvmName("batchD2sAverageWithAxis")
fun Batch<IOType.D3>.average(axis: Int): Batch<IOType.D2> = Batch(size) { this[it].average(axis) }

@JvmName("batchD3sBatchAverage")
fun Batch<IOType.D3>.batchAverage(): IOType.D3 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
