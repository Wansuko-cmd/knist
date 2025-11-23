package com.wsr.batch.collecction.minmax

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.max.max
import com.wsr.core.d0

@JvmName("batchD1sMax")
fun Batch<IOType.D1>.max() = Batch(size) { IOType.d0(this[it].max()) }

@JvmName("batchD2sMax")
fun Batch<IOType.D2>.max() = Batch(size) { IOType.d0(this[it].max()) }

@JvmName("batchD3sMax")
fun Batch<IOType.D3>.max() = Batch(size) { IOType.d0(this[it].max()) }
