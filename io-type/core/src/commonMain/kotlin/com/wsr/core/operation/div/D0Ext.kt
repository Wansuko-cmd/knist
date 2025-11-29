package com.wsr.core.operation.div

import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.get

operator fun Float.div(other: IOType.D0) = IOType.d0(this / other.get())

operator fun Float.div(other: IOType.D1) = IOType.d1(other.shape) { i -> this / other[i] }

operator fun Float.div(other: IOType.D2) = IOType.d2(other.shape) { i, j -> this / other[i, j] }

operator fun Float.div(other: IOType.D3) = IOType.d3(other.shape) { i, j, k -> this / other[i, j, k] }

operator fun Float.div(other: IOType.D4) = IOType.d4(other.shape) { i, j, k, l -> this / other[i, j, k, l] }

operator fun IOType.D0.div(other: Float) = IOType.d0(get() / other)
