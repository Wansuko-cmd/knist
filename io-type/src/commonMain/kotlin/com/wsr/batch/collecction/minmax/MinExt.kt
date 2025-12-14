package com.wsr.batch.collecction.minmax

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.min.min
import com.wsr.core.d0

@JvmName("batchD1sMin")
fun Batch<IOType.D1>.min() = Batch(size) { IOType.d0(this[it].min()) }

@JvmName("batchD2sMin")
fun Batch<IOType.D2>.min() = Batch(size) { IOType.d0(this[it].min()) }

@JvmName("batchD3sMin")
fun Batch<IOType.D3>.min() = Batch(size) { IOType.d0(this[it].min()) }
