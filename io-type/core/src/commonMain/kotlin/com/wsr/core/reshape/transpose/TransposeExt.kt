package com.wsr.core.reshape.transpose

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

fun IOType.D2.transpose() = IOType.d2(shape.reversed()) { x, y -> this[y, x] }

fun IOType.D3.transpose(axisI: Int, axisJ: Int, axisK: Int): IOType.D3 {
    val shape = listOf(shape[axisI], shape[axisJ], shape[axisK])
    return when {
        axisI == 0 && axisJ == 1 && axisK == 2 -> this
        axisI == 0 && axisJ == 2 && axisK == 1 ->
            IOType.d3(shape) { i, j, k ->
                get(
                    i = i,
                    j = k,
                    k = j,
                )
            }

        axisI == 1 && axisJ == 0 && axisK == 2 ->
            IOType.d3(shape) { i, j, k ->
                get(
                    i = j,
                    j = i,
                    k = k,
                )
            }

        axisI == 1 && axisJ == 2 && axisK == 0 ->
            IOType.d3(shape) { i, j, k ->
                get(
                    i = k,
                    j = i,
                    k = j,
                )
            }

        axisI == 2 && axisJ == 0 && axisK == 1 ->
            IOType.d3(shape) { i, j, k ->
                get(
                    i = j,
                    j = k,
                    k = i,
                )
            }

        axisI == 2 && axisJ == 1 && axisK == 0 ->
            IOType.d3(shape) { i, j, k ->
                get(
                    i = k,
                    j = j,
                    k = i,
                )
            }

        else -> throw IllegalStateException()
    }
}
