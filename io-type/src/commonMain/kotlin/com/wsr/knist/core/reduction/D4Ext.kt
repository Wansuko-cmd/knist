package com.wsr.knist.core.reduction

import com.wsr.knist.Backend
import com.wsr.knist.core.D0
import com.wsr.knist.core.D3
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun IOType.D4.average(): IOType.D0.Global = IOType.D0(Backend.average(value))

@ScopeOp
fun IOType.D4.average(axis: Int): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, k, l),
        value = Backend.average(x = value, xi = i, xj = j, xk = k * l, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, k, l),
        value = Backend.average(x = value, xi = i, xj = j, xk = k * l, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.average(x = value, xi = i * j, xj = k, xk = l, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, k),
        value = Backend.average(x = value, xi = i * j, xj = k, xk = l, axis = 2),
    )
}

@ScopeOp
fun IOType.D4.max(): IOType.D0.Global = IOType.D0(Backend.max(x = value))

@ScopeOp
fun IOType.D4.max(axis: Int): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, k, l),
        value = Backend.max(x = value, xi = i, xj = j, xk = k * l, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, k, l),
        value = Backend.max(x = value, xi = i, xj = j, xk = k * l, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.max(x = value, xi = i * j, xj = k, xk = l, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, k),
        value = Backend.max(x = value, xi = i * j, xj = k, xk = l, axis = 2),
    )
}

@ScopeOp
fun IOType.D4.min(): IOType.D0.Global = IOType.D0(Backend.min(x = value))

@ScopeOp
fun IOType.D4.min(axis: Int): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, k, l),
        value = Backend.min(x = value, xi = i, xj = j, xk = k * l, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, k, l),
        value = Backend.min(x = value, xi = i, xj = j, xk = k * l, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.min(x = value, xi = i * j, xj = k, xk = l, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, k),
        value = Backend.min(x = value, xi = i * j, xj = k, xk = l, axis = 2),
    )
}

@ScopeOp
fun IOType.D4.sum(): IOType.D0.Global = IOType.D0(Backend.sum(value))

@ScopeOp
fun IOType.D4.sum(axis: Int): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, k, l),
        value = Backend.sum(x = value, xi = i, xj = j, xk = k * l, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, k, l),
        value = Backend.sum(x = value, xi = i, xj = j, xk = k * l, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.sum(x = value, xi = i * j, xj = k, xk = l, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, k),
        value = Backend.sum(x = value, xi = i * j, xj = k, xk = l, axis = 2),
    )
}

@ScopeOp
fun IOType.D4.maxIndex(axis: Int): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, k, l),
        value = Backend.maxIndex(x = value, xi = i, xj = j, xk = k * l, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, k, l),
        value = Backend.maxIndex(x = value, xi = i, xj = j, xk = k * l, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.maxIndex(x = value, xi = i * j, xj = k, xk = l, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, k),
        value = Backend.maxIndex(x = value, xi = i * j, xj = k, xk = l, axis = 2),
    )
}

@ScopeOp
fun IOType.D4.topK(k: Int, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, this.k, l),
        value = Backend.topK(x = value, xi = i, xj = j, xk = this.k * l, k = k, random = random, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, this.k, l),
        value = Backend.topK(x = value, xi = i, xj = j, xk = this.k * l, k = k, random = random, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.topK(x = value, xi = i * j, xj = this.k, xk = l, k = k, random = random, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, this.k),
        value = Backend.topK(x = value, xi = i * j, xj = this.k, xk = l, k = k, random = random, axis = 2),
    )
}

@ScopeOp
fun IOType.D4.topP(p: Float, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D3.Global = when (axis) {
    0 -> IOType.D3(
        shape = listOf(j, k, l),
        value = Backend.topP(x = value, xi = i, xj = j, xk = k * l, p = p, random = random, axis = 0),
    )

    1 -> IOType.D3(
        shape = listOf(i, k, l),
        value = Backend.topP(x = value, xi = i, xj = j, xk = k * l, p = p, random = random, axis = 1),
    )

    2 -> IOType.D3(
        shape = listOf(i, j, l),
        value = Backend.topP(x = value, xi = i * j, xj = k, xk = l, p = p, random = random, axis = 1),
    )

    else -> IOType.D3(
        shape = listOf(i, j, k),
        value = Backend.topP(x = value, xi = i * j, xj = k, xk = l, p = p, random = random, axis = 2),
    )
}
