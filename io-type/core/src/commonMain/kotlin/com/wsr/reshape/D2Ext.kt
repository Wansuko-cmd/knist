package com.wsr.reshape

import com.wsr.IOType

fun List<IOType.D2>.toD3(): IOType.D3 = IOType.d3(
    x = size,
    y = first().shape[0],
    z = first().shape[1],
) { x, y, z -> this[x][y, z] }

fun IOType.D2.transpose() = IOType.d2(shape.reversed()) { x, y -> this[y, x] }

fun List<IOType.D2>.transpose() = this.map { it.transpose() }
