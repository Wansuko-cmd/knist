package com.wsr.d2

import com.wsr.IOType

operator fun IOType.D2.plus(other: IOType.D2) = IOType.d2(shape) { x, y -> this[x, y] + other[x, y] }

operator fun IOType.D2.minus(other: IOType.D2) = IOType.d2(shape) { x, y -> this[x, y] - other[x, y] }

operator fun List<IOType.D2>.plus(other: IOType.D2) = List(size) { this[it] + other }

operator fun List<IOType.D2>.minus(other: IOType.D2) = List(size) { this[it] - other }

operator fun List<IOType.D2>.minus(other: List<IOType.D2>) = List(size) { this[it] - other[it] }

operator fun Double.times(other: IOType.D2) = IOType.d2(other.shape) { x, y -> this * other[x, y] }

operator fun Double.times(other: List<IOType.D2>) = List(other.size) { this * other[it] }
