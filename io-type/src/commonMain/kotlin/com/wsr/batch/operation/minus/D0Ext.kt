package com.wsr.batch.operation.minus

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapWith
import com.wsr.core.IOType
import com.wsr.core.operation.minus.minus

@JvmName("batchFloatMinusD1s")
operator fun Batch<IOType.D0>.minus(other: Batch<IOType.D0>) = this.mapWith(other) { a, b -> a - b }
