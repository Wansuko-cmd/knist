package com.wsr.batch.minmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.max
import com.wsr.d0
import com.wsr.get
@JvmName("batchD1sMax")
fun Batch<IOType.D1>.max() = Batch(size) { IOType.d0(this[it].max()) }

@JvmName("batchD2sMax")
fun Batch<IOType.D2>.max() = Batch(size) { IOType.d0(this[it].max()) }

@JvmName("batchD3sMax")
fun Batch<IOType.D3>.max() = Batch(size) { IOType.d0(this[it].max()) }
