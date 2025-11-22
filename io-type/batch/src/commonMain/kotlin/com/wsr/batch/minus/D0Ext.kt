package com.wsr.batch.minus

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.mapWith
import com.wsr.operator.minus

@JvmName("batchFloatMinusD1s")
operator fun Batch<IOType.D0>.minus(other: Batch<IOType.D0>) = this.mapWith(other) { a, b -> a - b }
