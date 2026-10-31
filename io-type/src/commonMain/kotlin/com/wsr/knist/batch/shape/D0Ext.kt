package com.wsr.knist.batch.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d0
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.get
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

fun IOType.D1.toBatch(): Batch<IOType.D0> = Batch.d0(size = i, value = value)

@JvmName("batchD0sToList")
fun Batch<IOType.D0>.toList(): List<IOType.D0> = List(size) { get(it) }

@ScopeOp
fun Batch<IOType.D0>.broadcastToD1(size: Int): Batch<IOType.D1.Global> {
    val result = Backend.gather(x = DataBuffer.create(size), y = value, i = this.size, j = 1, k = 1)
    return Batch.d1(this.size, size, result)
}

@ScopeOp
fun Batch<IOType.D0>.broadcastToD2(shape: List<Int>): Batch<IOType.D2.Global> {
    val size = shape.reduce { acc, i -> acc * i }
    val result = Backend.gather(x = DataBuffer.create(size), y = value, i = this.size, j = 1, k = 1)
    return Batch.d2(this.size, shape, result)
}

@ScopeOp
fun Batch<IOType.D0>.broadcastToD3(shape: List<Int>): Batch<IOType.D3.Global> {
    val size = shape.reduce { acc, i -> acc * i }
    val result = Backend.gather(x = DataBuffer.create(size), y = value, i = this.size, j = 1, k = 1)
    return Batch.d3(this.size, shape, result)
}
