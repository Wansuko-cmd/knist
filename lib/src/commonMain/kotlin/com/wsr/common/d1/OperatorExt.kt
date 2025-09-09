package com.wsr.common.d1

import com.wsr.common.IOType

operator fun IOType.D1.plus(other: IOType.D1) = IOType.d1(shape[0]) { this[it] + other[it] }

operator fun IOType.D1.minus(other: IOType.D1) = IOType.d1(shape[0]) { this[it] - other[it] }

operator fun List<IOType.D1>.plus(other: IOType.D1) = List(size) { this[it] + other }

operator fun List<IOType.D1>.minus(other: IOType.D1) = List(size) { this[it] - other }

operator fun List<IOType.D1>.plus(other: List<IOType.D1>) = List(size) { this[it] + other[it] }

operator fun List<IOType.D1>.minus(other: List<IOType.D1>) = List(size) { this[it] - other[it] }

operator fun Double.times(other: IOType.D1) = IOType.d1(other.shape[0]) { this * other[it] }

operator fun Double.times(other: List<IOType.D1>) = List(other.size) { this * other[it] }
