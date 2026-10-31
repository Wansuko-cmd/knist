package com.wsr.knist.core.shape

import com.wsr.knist.Backend
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

fun IOType.D3.reshapeToD2(i: Int, j: Int): IOType.D2.Global = IOType.D2(shape = listOf(i, j), value = value)

fun IOType.D3.reshapeToD4(i: Int, j: Int, k: Int, l: Int): IOType.D4.Global = IOType.D4(shape = listOf(i, j, k, l), value = value)
