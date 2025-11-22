package com.wsr.batch.average

import com.wsr.Batch
import com.wsr.IOType

@JvmName("batchD0sBatchAverage")
fun Batch<IOType.D0>.batchAverage(): IOType.D0 = IOType.d0(value.average().toFloat())
