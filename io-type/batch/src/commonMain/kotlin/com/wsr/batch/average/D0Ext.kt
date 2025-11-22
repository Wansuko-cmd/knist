package com.wsr.batch.average

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.operator.div
import com.wsr.operator.plus

@JvmName("batchD0sBatchAverage")
fun Batch<IOType.D0>.batchAverage(): IOType.D0 {
    var sum = this[0]
    for (i in 1 until size) sum += this[i]
    return sum / size.toFloat()
}
