package com.wsr.batch.operation.inner

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.operation.inner.inner

@JvmName("batchD1sInnerToD1s")
infix fun Batch<IOType.D1>.inner(other: Batch<IOType.D1>) = Batch(size) { IOType.d0(this[it] inner other[it]) }
