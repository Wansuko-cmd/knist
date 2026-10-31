package com.wsr.knist.batch.elementwise.math

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.elementwise.operation.minus.minus
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD4
import com.wsr.knist.core.IOType
import com.wsr.knist.core.elementwise.math.softmax
import com.wsr.knist.core.shape.reshapeToD2
import com.wsr.knist.core.shape.reshapeToD4
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchD3sExp")
@ScopeOp
fun Batch<IOType.D3>.exp(): Batch<IOType.D3.Global> {
    val result = Backend.exp(x = value)
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sLn")
@ScopeOp
fun Batch<IOType.D3>.ln(@ScopeOpDefault("1e-7f")e: Float = 1e-7f): Batch<IOType.D3.Global> {
    val result = Backend.ln(x = value, e = e)
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sPow")
@ScopeOp
fun Batch<IOType.D3>.pow(n: Int): Batch<IOType.D3.Global> {
    val result = Backend.pow(x = value, n = n)
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sSigmoid")
@ScopeOp
fun Batch<IOType.D3>.sigmoid(): Batch<IOType.D3.Global> = Batch.d3(size, shape, Backend.sigmoid(value))

@JvmName("batchD3sSoftmax")
@ScopeOp
fun Batch<IOType.D3>.softmax(): Batch<IOType.D3.Global> {
    val result = toD4().reshapeToD2(i = size, j = step).softmax(axis = 1).value
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sSoftmaxWithAxis")
@ScopeOp
fun Batch<IOType.D3>.softmax(axis: Int): Batch<IOType.D3.Global> {
    val result = toD4().softmax(axis = axis + 1).value
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sSqrt")
@ScopeOp
fun Batch<IOType.D3>.sqrt(@ScopeOpDefault("1e-7f")e: Float = 1e-7f): Batch<IOType.D3.Global> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sTanh")
@ScopeOp
fun Batch<IOType.D3>.tanh(): Batch<IOType.D3.Global> = 2f * (this * 2f).sigmoid() - 1f
