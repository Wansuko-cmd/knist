package com.wsr.core.reshape.transpose

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d4
import com.wsr.core.get

fun IOType.D2.transpose(): IOType.D2 {
    val result = Backend.transpose(x = value, xi = i, xj = j)
    return IOType.D2(shape = listOf(j, i), value = result)
}

fun IOType.D3.transpose(axisI: Int, axisJ: Int, axisK: Int): IOType.D3 {
    val result = Backend.transpose(x = value, xi = i, xj = j, xk = k, axisI = axisI, axisJ = axisJ, axisK = axisK)
    return IOType.D3(shape = listOf(shape[axisI], shape[axisJ], shape[axisK]), value = result)
}

fun IOType.D4.transpose(axisI: Int, axisJ: Int, axisK: Int, axisL: Int): IOType.D4 {
    val axes = listOf(axisI, axisJ, axisK, axisL)
    return IOType.d4(i = shape[axisI], j = shape[axisJ], k = shape[axisK], l = shape[axisL]) { i, j, k, l ->
        val indices = listOf(i, j, k, l)
        this[
            indices[axes.indexOf(0)],
            indices[axes.indexOf(1)],
            indices[axes.indexOf(2)],
            indices[axes.indexOf(3)],
        ]
    }
}
