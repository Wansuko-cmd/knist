package com.wsr.knist.batch.reduction

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d0
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName
import kotlin.random.Random

@JvmName("batchD1sSum")
@ScopeOp
fun Batch<IOType.D1>.sum(): Batch<IOType.D0.Global> {
    val result = Backend.sum(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD1sMax")
@ScopeOp
fun Batch<IOType.D1>.max(): Batch<IOType.D0.Global> {
    val result = Backend.max(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD1sMin")
@ScopeOp
fun Batch<IOType.D1>.min(): Batch<IOType.D0.Global> {
    val result = Backend.min(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD1sMaxIndex")
@ScopeOp
fun Batch<IOType.D1>.maxIndex(): Batch<IOType.D0.Global> {
    val result = Backend.maxIndex(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD1sTopK")
@ScopeOp
fun Batch<IOType.D1>.topK(k: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D0.Global> {
    val result = Backend.topK(x = value, xi = size, xj = step, k = k, axis = 1, random = random)
    return Batch.d0(size, result)
}

@JvmName("batchD1sTopP")
@ScopeOp
fun Batch<IOType.D1>.topP(p: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D0.Global> {
    val result = Backend.topP(x = value, xi = size, xj = step, p = p, axis = 1, random = random)
    return Batch.d0(size, result)
}
