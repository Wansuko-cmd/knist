package com.wsr.batch.inner

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.dot.inner.inner
import com.wsr.get

@JvmName("batchD1sInnerToD1s")
infix fun Batch<IOType.D1>.inner(other: Batch<IOType.D1>) = Batch(size) { IOType.d0(this[it] inner other[it]) }
