package com.wsr.core.operation.minus

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get

operator fun IOType.D1.minus(other: Float): IOType.D1 = IOType.d1(shape) { this[it] - other }

operator fun IOType.D1.minus(other: IOType.D0): IOType.D1 = IOType.d1(shape) { this[it] - other.get() }

operator fun IOType.D1.minus(other: IOType.D1): IOType.D1 = IOType.d1(shape) { this[it] - other[it] }