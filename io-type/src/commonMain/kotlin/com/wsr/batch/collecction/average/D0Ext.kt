package com.wsr.batch.collecction.average

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus

@JvmName("batchD0sBatchAverage")
fun Batch<IOType.D0>.batchAverage(): IOType.D0 {
    val sum = Backend.sum(x = value)
    return IOType.d0(sum / size.toFloat())
}
