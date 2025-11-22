package com.wsr.batch.average

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.average
import com.wsr.d0
import com.wsr.get
import com.wsr.operator.div
import com.wsr.operator.plus

@JvmName("batchD1sAverageBatch")
fun Batch<IOType.D1>.average(): Batch<IOType.D0> = Batch(size) { IOType.d0(this[it].average()) }

@JvmName("batchD1sBatchAverage")
fun Batch<IOType.D1>.batchAverage(): IOType.D1 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
