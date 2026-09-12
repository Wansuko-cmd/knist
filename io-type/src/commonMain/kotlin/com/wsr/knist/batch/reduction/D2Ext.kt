package com.wsr.knist.batch.reduction

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d0
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName
import kotlin.random.Random

@JvmName("batchD2sSum")
@ScopeOp
fun Batch<IOType.D2>.sum(): Batch<IOType.D0.Global> {
    val result = Backend.sum(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD2sSumWithAxis")
@ScopeOp
fun Batch<IOType.D2>.sum(axis: Int): Batch<IOType.D1.Global> {
    val result = Backend.sum(x = value, xi = size, xj = i, xk = j, axis = axis + 1)
    return Batch.d1(size, if (axis == 0) j else i, result)
}

@JvmName("batchD2sMax")
@ScopeOp
fun Batch<IOType.D2>.max(): Batch<IOType.D0.Global> {
    val result = Backend.max(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD2sMin")
@ScopeOp
fun Batch<IOType.D2>.min(): Batch<IOType.D0.Global> {
    val result = Backend.min(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD2sMaxIndexWithAxis")
@ScopeOp
fun Batch<IOType.D2>.maxIndex(axis: Int): Batch<IOType.D1.Global> {
    val result = Backend.maxIndex(x = value, xi = size, xj = i, xk = j, axis = axis + 1)
    return Batch.d1(size, if (axis == 0) j else i, result)
}

@JvmName("batchD2sTopKWithAxis")
@ScopeOp
fun Batch<IOType.D2>.topK(k: Int, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D1.Global> {
    val result = Backend.topK(x = value, xi = size, xj = i, xk = j, k = k, axis = axis + 1, random = random)
    return Batch.d1(size, if (axis == 0) j else i, result)
}

@JvmName("batchD2sTopPWithAxis")
@ScopeOp
fun Batch<IOType.D2>.topP(p: Float, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D1.Global> {
    val result = Backend.topP(x = value, xi = size, xj = i, xk = j, p = p, axis = axis + 1, random = random)
    return Batch.d1(size, if (axis == 0) j else i, result)
}
