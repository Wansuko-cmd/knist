package com.wsr.batch.minmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.min
import com.wsr.d0
import com.wsr.get

@JvmName("batchD1sMin")
fun Batch<IOType.D1>.min() = Batch(size) { IOType.d0(this[it].min()) }

@JvmName("batchD2sMin")
fun Batch<IOType.D2>.min() = Batch(size) { IOType.d0(this[it].min()) }

@JvmName("batchD3sMin")
fun Batch<IOType.D3>.min() = Batch(size) { IOType.d0(this[it].min()) }
