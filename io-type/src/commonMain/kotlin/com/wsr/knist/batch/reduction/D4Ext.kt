package com.wsr.knist.batch.reduction

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.batch.l
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName
import kotlin.random.Random

@JvmName("batchD4sMaxWithAxis")
@ScopeOp
fun Batch<IOType.D4>.max(axis: Int): Batch<IOType.D3.Global> = when (axis) {
    0 -> Batch.d3(
        size,
        j,
        k,
        l,
        Backend.max(x = value, xi = size, xj = i, xk = j * k * l, axis = 1),
    )

    1 -> Batch.d3(
        size,
        i,
        k,
        l,
        Backend.max(x = value, xi = size * i, xj = j, xk = k * l, axis = 1),
    )

    2 -> Batch.d3(
        size,
        i,
        j,
        l,
        Backend.max(x = value, xi = size * i * j, xj = k, xk = l, axis = 1),
    )

    3 -> Batch.d3(
        size,
        i,
        j,
        k,
        Backend.max(x = value, xi = size, xj = i * j * k, xk = l, axis = 2),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1, 2 or 3.")
}

@JvmName("batchD4sMaxIndexWithAxis")
@ScopeOp
fun Batch<IOType.D4>.maxIndex(axis: Int): Batch<IOType.D3.Global> = when (axis) {
    0 -> Batch.d3(
        size,
        j,
        k,
        l,
        Backend.maxIndex(x = value, xi = size, xj = i, xk = j * k * l, axis = 1),
    )

    1 -> Batch.d3(
        size,
        i,
        k,
        l,
        Backend.maxIndex(x = value, xi = size * i, xj = j, xk = k * l, axis = 1),
    )

    2 -> Batch.d3(
        size,
        i,
        j,
        l,
        Backend.maxIndex(x = value, xi = size * i * j, xj = k, xk = l, axis = 1),
    )

    3 -> Batch.d3(
        size,
        i,
        j,
        k,
        Backend.maxIndex(x = value, xi = size, xj = i * j * k, xk = l, axis = 2),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1, 2 or 3.")
}

@JvmName("batchD4sTopKAxis")
@ScopeOp
fun Batch<IOType.D4>.topK(k: Int, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D3.Global> = when (axis) {
    0 -> Batch.d3(
        size,
        j,
        this.k,
        l,
        Backend.topK(x = value, xi = size, xj = i, xk = j * this.k * l, k = k, axis = 1, random = random),
    )

    1 -> Batch.d3(
        size,
        i,
        this.k,
        l,
        Backend.topK(x = value, xi = size * i, xj = j, xk = this.k * l, k = k, axis = 1, random = random),
    )

    2 -> Batch.d3(
        size,
        i,
        j,
        l,
        Backend.topK(x = value, xi = size * i * j, xj = this.k, xk = l, k = k, axis = 1, random = random),
    )

    3 -> Batch.d3(
        size,
        i,
        j,
        this.k,
        Backend.topK(x = value, xi = size, xj = i * j * this.k, xk = l, k = k, axis = 2, random = random),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1, 2 or 3.")
}

@JvmName("batchD4sTopPAxis")
@ScopeOp
fun Batch<IOType.D4>.topP(p: Float, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D3.Global> = when (axis) {
    0 -> Batch.d3(
        size,
        j,
        k,
        l,
        Backend.topP(x = value, xi = size, xj = i, xk = j * k * l, p = p, axis = 1, random = random),
    )

    1 -> Batch.d3(
        size,
        i,
        k,
        l,
        Backend.topP(x = value, xi = size * i, xj = j, xk = k * l, p = p, axis = 1, random = random),
    )

    2 -> Batch.d3(
        size,
        i,
        j,
        l,
        Backend.topP(x = value, xi = size * i * j, xj = k, xk = l, p = p, axis = 1, random = random),
    )

    3 -> Batch.d3(
        size,
        i,
        j,
        k,
        Backend.topP(x = value, xi = size, xj = i * j * k, xk = l, p = p, axis = 2, random = random),
    )

    else -> throw IllegalArgumentException("axis is $axis, not 0, 1, 2 or 3.")
}
