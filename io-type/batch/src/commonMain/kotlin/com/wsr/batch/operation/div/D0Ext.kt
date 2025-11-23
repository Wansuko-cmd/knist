package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchFloatDivD0s")
operator fun Float.div(other: Batch<IOType.D0>): Batch<IOType.D0> = other.map { this / it }
