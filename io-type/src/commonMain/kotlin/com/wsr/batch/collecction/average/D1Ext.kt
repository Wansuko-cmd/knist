package com.wsr.batch.collecction.average

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.core.d0
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD1sAverageBatch")
fun Batch<IOType.D1>.average(): Batch<IOType.D0> = Batch(size) { IOType.d0(this[it].average()) }

@JvmName("batchD1sBatchAverage")
fun Batch<IOType.D1>.batchAverage(): IOType.D1 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
