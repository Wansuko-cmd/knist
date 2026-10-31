package com.wsr.knist.batch.elementwise.math

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.elementwise.operation.minus.minus
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD3
import com.wsr.knist.core.IOType
import com.wsr.knist.core.elementwise.math.softmax
import com.wsr.knist.core.shape.reshapeToD2
import com.wsr.knist.core.shape.reshapeToD3
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchD2sExp")
@ScopeOp
fun Batch<IOType.D2>.exp(): Batch<IOType.D2.Global> {
    val result = Backend.exp(x = value)
    return Batch.d2(size, shape, result)
}

@JvmName("batchD2sLn")
@ScopeOp
fun Batch<IOType.D2>.ln(@ScopeOpDefault("1e-7f") e: Float = 1e-7f): Batch<IOType.D2.Global> {
    val result = Backend.ln(x = value, e = e)
    return Batch.d2(size, shape, result)
}

@JvmName("batchD2sPow")
@ScopeOp
fun Batch<IOType.D2>.pow(n: Int): Batch<IOType.D2.Global> {
    val result = Backend.pow(x = value, n = n)
    return Batch.d2(size, shape, result)
}

@JvmName("batchD2sSigmoid")
@ScopeOp
fun Batch<IOType.D2>.sigmoid(): Batch<IOType.D2.Global> = Batch.d2(size, shape, Backend.sigmoid(value))

@JvmName("batchD2sSoftmax")
@ScopeOp
fun Batch<IOType.D2>.softmax(): Batch<IOType.D2.Global> {
    val result = toD3().reshapeToD2(i = size, j = step).softmax(axis = 1).value
    return Batch.d2(size, shape, result)
}

@JvmName("batchD2sSoftmaxWithAxis")
@ScopeOp
fun Batch<IOType.D2>.softmax(axis: Int): Batch<IOType.D2.Global> {
    val result = toD3().softmax(axis = axis + 1).value
    return Batch.d2(size, shape, result)
}

@JvmName("batchD2sSqrt")
@ScopeOp
fun Batch<IOType.D2>.sqrt(@ScopeOpDefault("1e-7f") e: Float = 1e-7f): Batch<IOType.D2.Global> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch.d2(size, shape, result)
}

@JvmName("batchD2sTanh")
@ScopeOp
fun Batch<IOType.D2>.tanh(): Batch<IOType.D2.Global> = 2f * (this * 2f).sigmoid() - 1f
