package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.operator.plus

@JvmName("batchD1sPlusD1")
operator fun Batch<IOType.D1>.plus(other: IOType.D1) = map { it + other }
