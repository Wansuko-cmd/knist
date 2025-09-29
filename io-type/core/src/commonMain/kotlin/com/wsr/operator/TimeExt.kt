package com.wsr.operator

import com.wsr.IOType

operator fun Double.times(other: IOType.D1) = IOType.d1(other.shape[0]) { this * other[it] }

@JvmName("timesToD1s")
operator fun Double.times(other: List<IOType.D1>) = other.map { this * it }

operator fun Double.times(other: IOType.D2) = IOType.d2(other.shape) { x, y -> this * other[x, y] }

@JvmName("timesToD2s")
operator fun Double.times(other: List<IOType.D2>) = other.map { this * it }

operator fun Double.times(other: IOType.D3) =
    IOType.d3(other.shape) { x, y, z -> this * other[x, y, z] }
