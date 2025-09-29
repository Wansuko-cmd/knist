package com.wsr.d2

import com.wsr.IOType




operator fun Double.times(other: IOType.D2) = IOType.d2(other.shape) { x, y -> this * other[x, y] }

operator fun Double.times(other: List<IOType.D2>) = List(other.size) { this * other[it] }
