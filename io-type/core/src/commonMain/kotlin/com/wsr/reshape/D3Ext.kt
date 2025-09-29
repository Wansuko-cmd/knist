package com.wsr.reshape

import com.wsr.IOType

fun IOType.D3.transpose(
    axisX: Int,
    axisY: Int,
    axisZ: Int,
): IOType.D3 {
    val shape = listOf(shape[axisX], shape[axisY], shape[axisZ])
    return when {
        axisX == 0 && axisY == 1 && axisZ == 2 -> this
        axisX == 0 && axisY == 2 && axisZ == 1 -> IOType.d3(shape) { x, y, z -> get(x = x, y = z, z = y) }
        axisX == 1 && axisY == 0 && axisZ == 2 -> IOType.d3(shape) { x, y, z -> get(x = y, y = x, z = z) }
        axisX == 1 && axisY == 2 && axisZ == 0 -> IOType.d3(shape) { x, y, z -> get(x = z, y = x, z = y) }
        axisX == 2 && axisY == 0 && axisZ == 1 -> IOType.d3(shape) { x, y, z -> get(x = y, y = z, z = x) }
        axisX == 2 && axisY == 1 && axisZ == 0 -> IOType.d3(shape) { x, y, z -> get(x = z, y = y, z = x) }
        else -> throw IllegalStateException()
    }
}
