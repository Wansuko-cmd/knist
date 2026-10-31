package com.wsr.knist.core.shape

import com.wsr.knist.Backend
import com.wsr.knist.core.D1
import com.wsr.knist.core.D2
import com.wsr.knist.core.D3
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp

@ScopeOp
fun IOType.D3.transpose(axisI: Int, axisJ: Int, axisK: Int): IOType.D3.Global {
    val result = Backend.transpose(x = value, xi = i, xj = j, xk = k, axisI = axisI, axisJ = axisJ, axisK = axisK)
    return IOType.D3(shape = listOf(shape[axisI], shape[axisJ], shape[axisK]), value = result)
}

@ScopeOp
fun IOType.D3.flip(axis: Int): IOType.D3.Global {
    val result = Backend.flip(x = value, xi = i, xj = j, xk = k, axis = axis)
    return IOType.D3(shape = shape, value = result)
}

fun IOType.D3.reshapeToD1(): IOType.D1 = IOType.D1(value = value)

fun IOType.D3.reshapeToD2(i: Int, j: Int): IOType.D2 = reshapeToD2(listOf(i, j))

fun IOType.D3.reshapeToD2(shape: List<Int>): IOType.D2 = IOType.D2(shape = shape, value = value)

fun IOType.D3.reshapeToD3(i: Int, j: Int, k: Int): IOType.D3 = reshapeToD3(listOf(i, j, k))

fun IOType.D3.reshapeToD3(shape: List<Int>): IOType.D3 = IOType.D3(shape = shape, value = value)

fun IOType.D3.reshapeToD4(i: Int, j: Int, k: Int, l: Int): IOType.D4 = reshapeToD4(listOf(i, j, k, l))

fun IOType.D3.reshapeToD4(shape: List<Int>): IOType.D4 = IOType.D4(shape = shape, value = value)
