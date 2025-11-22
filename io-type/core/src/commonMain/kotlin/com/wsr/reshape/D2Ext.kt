package com.wsr.reshape

import com.wsr.IOType
import com.wsr.d2
import com.wsr.d3
import com.wsr.get

fun List<IOType.D2>.toD3(): IOType.D3 = IOType.d3(
    i = size,
    j = first().shape[0],
    k = first().shape[1],
) { i, j, k -> this[i][j, k] }

fun IOType.D2.transpose() = IOType.d2(shape.reversed()) { x, y -> this[y, x] }

fun List<IOType.D2>.transpose() = this.map { it.transpose() }
