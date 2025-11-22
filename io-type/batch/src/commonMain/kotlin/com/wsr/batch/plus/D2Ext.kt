package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.operator.plus

@JvmName("batchD2sPlusD2")
operator fun Batch<IOType.D2>.plus(other: IOType.D2) = map { it + other }
