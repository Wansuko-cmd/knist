package com.wsr.d3

import com.wsr.IOType

operator fun IOType.D3.plus(other: IOType.D3) = IOType.d3(shape) { x, y, z -> this[x, y, z] + other[x, y, z] }

operator fun IOType.D3.minus(other: IOType.D3) = IOType.d3(shape) { x, y, z -> this[x, y, z] - other[x, y, z] }

operator fun List<IOType.D3>.plus(other: IOType.D3) = List(size) { this[it] + other }

operator fun List<IOType.D3>.minus(other: IOType.D3) = List(size) { this[it] - other }

operator fun List<IOType.D3>.minus(other: List<IOType.D3>) = List(size) { this[it] - other[it] }

operator fun Double.times(other: IOType.D3) = IOType.d3(other.shape) { x, y, z -> this * other[x, y, z] }

operator fun Double.times(other: List<IOType.D3>) = List(other.size) { this * other[it] }
