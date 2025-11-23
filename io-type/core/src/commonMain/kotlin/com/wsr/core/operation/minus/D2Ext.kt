package com.wsr.core.operation.minus

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get

operator fun IOType.D2.minus(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other }

operator fun IOType.D2.minus(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other.get() }

operator fun IOType.D2.minus(other: IOType.D1): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other[i] }

operator fun IOType.D2.minus(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] - other[i, j] }