package com.wsr.batch.operation.div

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div

@JvmName("batchD3sDivD0s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D0>): Batch<IOType.D3> = Batch(size) { this[it] / other[it] }

@JvmName("batchD3sDivD3")
operator fun Batch<IOType.D3>.div(other: IOType.D3) = map { it / other }

@JvmName("batchD3sDivD3s")
operator fun Batch<IOType.D3>.div(other: Batch<IOType.D3>) = mapWith(other) { a, b -> a / b }
