package com.wsr.core.operation.minus

import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

operator fun Float.minus(other: IOType.D1): IOType.D1 = IOType.d1(other.shape) { this - other[it] }

operator fun Float.minus(other: IOType.D2): IOType.D2 = IOType.d2(other.shape) { i, j -> this - other[i, j] }

operator fun Float.minus(other: IOType.D3): IOType.D3 = IOType.d3(other.shape) { i, j, k -> this - other[i, j, k] }

operator fun IOType.D0.minus(other: IOType.D0) = IOType.d0(get() - other.get())