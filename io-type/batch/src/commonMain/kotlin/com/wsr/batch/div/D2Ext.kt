package com.wsr.batch.div

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.operator.div

@JvmName("batchD2sDivD2")
operator fun Batch<IOType.D2>.div(other: IOType.D2) = map { it / other }

@JvmName("batchD2sDivD2s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D2>) = mapWith(other) { a, b -> a / b }
