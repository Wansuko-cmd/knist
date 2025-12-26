package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.get

operator fun Float.times(other: IOType.D0) = IOType.d0(this * other.get())

operator fun Float.times(other: IOType.D1): IOType.D1 = IOType.d1(other.shape) { this * other[it] }

operator fun Float.times(other: IOType.D2): IOType.D2 = IOType.d2(other.shape) { i, j -> this * other[i, j] }

operator fun Float.times(other: IOType.D3): IOType.D3 = IOType.d3(other.shape) { i, j, k -> this * other[i, j, k] }

operator fun Float.times(other: IOType.D4): IOType.D4 = IOType.d4(other.shape) { i, j, k, l ->
    this * other[i, j, k, l]
}

operator fun IOType.D0.times(other: Float) = IOType.d0(get() * other)

operator fun IOType.D0.times(other: IOType.D0) = IOType.d0(get() * other.get())

operator fun IOType.D0.times(other: IOType.D1) = this.get() * other

operator fun IOType.D0.times(other: IOType.D2) = this.get() * other

operator fun IOType.D0.times(other: IOType.D3) = this.get() * other

operator fun IOType.D0.times(other: IOType.D4) = this.get() * other
