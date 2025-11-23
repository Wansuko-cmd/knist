package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get

operator fun IOType.D2.times(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other }

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other[i, j] }