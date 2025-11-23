package com.wsr.core.operation.div

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get

operator fun IOType.D2.div(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] / other }

operator fun IOType.D2.div(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] / other.get() }

operator fun IOType.D2.div(other: IOType.D1): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] / other[i] }

operator fun IOType.D2.div(other: IOType.D2): IOType.D2 = IOType.d2(this.shape) { i, j -> this[i, j] / other[i, j] }