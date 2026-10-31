package com.wsr.knist.core.shape

import com.wsr.knist.Backend
import com.wsr.knist.core.D1
import com.wsr.knist.core.D2
import com.wsr.knist.core.D3
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp

@ScopeOp
fun IOType.D4.transpose(axisI: Int, axisJ: Int, axisK: Int, axisL: Int): IOType.D4.Global {
    val result = Backend.transpose(
        x = value,
        xi = i,
        xj = j,
        xk = k,
        xl = l,
        axisI = axisI,
        axisJ = axisJ,
        axisK = axisK,
        axisL = axisL,
    )
    return IOType.D4(shape = listOf(shape[axisI], shape[axisJ], shape[axisK], shape[axisL]), value = result)
}

@ScopeOp
fun IOType.D4.flip(axis: Int): IOType.D4.Global {
    val result = when (axis) {
        0 -> Backend.flip(x = value, xi = i, xj = j, xk = k * l, axis = 0)
        1 -> Backend.flip(x = value, xi = i, xj = j, xk = k * l, axis = 1)
        2 -> Backend.flip(x = value, xi = i * j, xj = k, xk = l, axis = 1)
        else -> Backend.flip(x = value, xi = i, xj = j * k, xk = l, axis = 2)
    }
    return IOType.D4(shape = shape, value = result)
}

fun IOType.D4.reshapeToD1(): IOType.D1 = IOType.D1(value = value)

fun IOType.D4.reshapeToD2(i: Int, j: Int): IOType.D2 = reshapeToD2(listOf(i, j))

fun IOType.D4.reshapeToD2(shape: List<Int>): IOType.D2 = IOType.D2(shape = shape, value = value)

fun IOType.D4.reshapeToD3(i: Int, j: Int, k: Int): IOType.D3 = reshapeToD3(listOf(i, j, k))

fun IOType.D4.reshapeToD3(shape: List<Int>): IOType.D3 = IOType.D3(shape = shape, value = value)

fun IOType.D4.reshapeToD4(i: Int, j: Int, k: Int, l: Int): IOType.D4 = reshapeToD4(listOf(i, j, k, l))

fun IOType.D4.reshapeToD4(shape: List<Int>): IOType.D4 = IOType.D4(shape = shape, value = value)
