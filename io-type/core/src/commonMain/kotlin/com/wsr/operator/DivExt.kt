package com.wsr.operator

import com.wsr.IOType

operator fun IOType.D1.div(other: Double) = IOType.d1(shape[0]) { this[it] / other }

operator fun Double.div(other: IOType.D2) = IOType.d2(other.shape) { x, y -> this / other[x, y] }

operator fun Double.div(other: IOType.D3) =
    IOType.d3(other.shape) { x, y, z -> this / other[x, y, z] }
