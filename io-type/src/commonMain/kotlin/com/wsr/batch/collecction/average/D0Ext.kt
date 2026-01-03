package com.wsr.batch.collecction.average

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD0sBatchAverage")
fun Batch<IOType.D0>.batchAverage(): IOType.D0 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
