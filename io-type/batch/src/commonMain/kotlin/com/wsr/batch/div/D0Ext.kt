package com.wsr.batch.div

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.get
import com.wsr.operator.div
import com.wsr.set

@JvmName("batchFloatDivD0s")
operator fun Float.div(other: Batch<IOType.D0>): Batch<IOType.D0> = other.map { this / it }
