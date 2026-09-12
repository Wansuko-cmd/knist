package com.wsr.knist.batch.reduction

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d0
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName
import kotlin.random.Random

@JvmName("batchD3sSum")
@ScopeOp
fun Batch<IOType.D3>.sum(): Batch<IOType.D0.Global> {
    val result = Backend.sum(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD3sSumWithAxis")
@ScopeOp
fun Batch<IOType.D3>.sum(axis: Int): Batch<IOType.D2.Global> = when (axis) {
    0 -> Batch.d2(
        size,
        j,
        k,
        Backend.sum(x = value, xi = size, xj = i, xk = j * k, axis = 1),
    )

    1 -> Batch.d2(
        size,
        i,
        k,
        Backend.sum(x = value, xi = size * i, xj = j, xk = k, axis = 1),
    )

    2 -> Batch.d2(
        size,
        i,
        j,
        Backend.sum(x = value, xi = size, xj = i * j, xk = k, axis = 2),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1 or 2.")
}

@JvmName("batchD3sMax")
@ScopeOp
fun Batch<IOType.D3>.max(): Batch<IOType.D0.Global> {
    val result = Backend.max(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD3sMin")
@ScopeOp
fun Batch<IOType.D3>.min(): Batch<IOType.D0.Global> {
    val result = Backend.min(x = value, xi = size, xj = step, axis = 1)
    return Batch.d0(size, result)
}

@JvmName("batchD3sMaxWithAxis")
@ScopeOp
fun Batch<IOType.D3>.max(axis: Int): Batch<IOType.D2.Global> = when (axis) {
    0 -> Batch.d2(
        size,
        j,
        k,
        Backend.max(x = value, xi = size, xj = i, xk = j * k, axis = 1),
    )

    1 -> Batch.d2(
        size,
        i,
        k,
        Backend.max(x = value, xi = size * i, xj = j, xk = k, axis = 1),
    )

    2 -> Batch.d2(
        size,
        i,
        j,
        Backend.max(x = value, xi = size, xj = i * j, xk = k, axis = 2),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1 or 2.")
}

@JvmName("batchD3sMaxIndexWithAxis")
@ScopeOp
fun Batch<IOType.D3>.maxIndex(axis: Int): Batch<IOType.D2.Global> = when (axis) {
    0 -> Batch.d2(
        size,
        j,
        k,
        Backend.maxIndex(x = value, xi = size, xj = i, xk = j * k, axis = 1),
    )

    1 -> Batch.d2(
        size,
        i,
        k,
        Backend.maxIndex(x = value, xi = size * i, xj = j, xk = k, axis = 1),
    )

    2 -> Batch.d2(
        size,
        i,
        j,
        Backend.maxIndex(x = value, xi = size, xj = i * j, xk = k, axis = 2),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1 or 2.")
}

@JvmName("batchD3sTopKWithAxis")
@ScopeOp
fun Batch<IOType.D3>.topK(k: Int, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D2.Global> = when (axis) {
    0 -> Batch.d2(
        size,
        j,
        this.k,
        Backend.topK(x = value, xi = size, xj = i, xk = j * this.k, k = k, axis = 1, random = random),
    )

    1 -> Batch.d2(
        size,
        i,
        this.k,
        Backend.topK(x = value, xi = size * i, xj = j, xk = this.k, k = k, axis = 1, random = random),
    )

    2 -> Batch.d2(
        size,
        i,
        j,
        Backend.topK(x = value, xi = size, xj = i * j, xk = this.k, k = k, axis = 2, random = random),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1 or 2.")
}

@JvmName("batchD3sTopPWithAxis")
@ScopeOp
fun Batch<IOType.D3>.topP(p: Float, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D2.Global> = when (axis) {
    0 -> Batch.d2(
        size,
        j,
        k,
        Backend.topP(x = value, xi = size, xj = i, xk = j * k, p = p, axis = 1, random = random),
    )

    1 -> Batch.d2(
        size,
        i,
        k,
        Backend.topP(x = value, xi = size * i, xj = j, xk = k, p = p, axis = 1, random = random),
    )

    2 -> Batch.d2(
        size,
        i,
        j,
        Backend.topP(x = value, xi = size, xj = i * j, xk = k, p = p, axis = 2, random = random),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1 or 2.")
}
