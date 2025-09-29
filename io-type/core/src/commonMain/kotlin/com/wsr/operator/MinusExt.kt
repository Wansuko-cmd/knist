package com.wsr.operator

import com.wsr.IOType

operator fun IOType.D1.minus(other: IOType.D1) = IOType.d1(shape[0]) { this[it] - other[it] }

operator fun List<IOType.D1>.minus(other: IOType.D1) = List(size) { this[it] - other }

operator fun IOType.D2.minus(other: IOType.D2) =
    IOType.d2(shape) { x, y -> this[x, y] - other[x, y] }

operator fun List<IOType.D2>.minus(other: IOType.D2) = List(size) { this[it] - other }

operator fun IOType.D3.minus(other: IOType.D3) =
    IOType.d3(shape) { x, y, z -> this[x, y, z] - other[x, y, z] }

operator fun List<IOType.D3>.minus(other: IOType.D3) = List(size) { this[it] - other }
