package com.wsr.knist.batch.elementwise.math

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.elementwise.operation.minus.minus
import com.wsr.knist.batch.elementwise.operation.times.times
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD2
import com.wsr.knist.core.IOType
import com.wsr.knist.core.elementwise.math.exp
import com.wsr.knist.core.elementwise.math.softmax
import com.wsr.knist.core.elementwise.operation.minus.minus
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchD1sExp")
@ScopeOp
fun Batch<IOType.D1>.exp(): Batch<IOType.D1.Global> {
    val result = Backend.exp(x = value)
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sLn")
@ScopeOp
fun Batch<IOType.D1>.ln(@ScopeOpDefault("1e-7f") e: Float = 1e-7f): Batch<IOType.D1.Global> {
    val result = Backend.ln(x = value, e = e)
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sPow")
@ScopeOp
fun Batch<IOType.D1>.pow(n: Int): Batch<IOType.D1.Global> {
    val result = Backend.pow(x = value, n = n)
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sSigmoid")
@ScopeOp
fun Batch<IOType.D1>.sigmoid(): Batch<IOType.D1.Global> = Batch.d1(size, shape, Backend.sigmoid(value))

@JvmName("batchD1sSoftmax")
@ScopeOp
fun Batch<IOType.D1>.softmax(): Batch<IOType.D1.Global> {
    val result = toD2().softmax(axis = 1).value
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sSqrt")
@ScopeOp
fun Batch<IOType.D1>.sqrt(@ScopeOpDefault("1e-7f") e: Float = 1e-7f): Batch<IOType.D1.Global> {
    val result = Backend.sqrt(x = value, e = e)
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sTanh")
@ScopeOp
fun Batch<IOType.D1>.tanh(): Batch<IOType.D1.Global> = 2f * (this * 2f).sigmoid() - 1f
