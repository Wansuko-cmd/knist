package com.wsr.batch.plus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.operator.plus

@JvmName("batchD3sPlusD3")
operator fun Batch<IOType.D3>.plus(other: IOType.D3) = map { it + other }
