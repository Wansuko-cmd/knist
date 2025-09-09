package com.wsr.common.d2

import com.wsr.common.IOType

operator fun IOType.D2.minus(other: IOType.D2) = IOType.d2(shape[0], shape[1]) { x, y ->
    this[x, y] - other[x, y]
}

operator fun List<IOType.D2>.minus(other: List<IOType.D2>) = List(size) { this[it] - other[it] }
