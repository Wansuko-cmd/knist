package com.wsr.d3

import com.wsr.IOType





operator fun Double.times(other: IOType.D3) = IOType.d3(other.shape) { x, y, z -> this * other[x, y, z] }

operator fun Double.times(other: List<IOType.D3>) = List(other.size) { this * other[it] }
