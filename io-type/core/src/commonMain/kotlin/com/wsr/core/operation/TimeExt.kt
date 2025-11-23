package com.wsr.core.operation

import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

/**
 * Float
 */
operator fun Float.times(other: IOType.D0) = IOType.d0(this * other.get())

operator fun Float.times(other: IOType.D1): IOType.D1 = IOType.d1(other.shape) { this * other[it] }

operator fun Float.times(other: IOType.D2): IOType.D2 = IOType.d2(other.shape) { i, j -> this * other[i, j] }

operator fun Float.times(other: IOType.D3): IOType.D3 = IOType.d3(other.shape) { i, j, k -> this * other[i, j, k] }

/**
 * IOType.D0
 */
operator fun IOType.D0.times(other: Float) = IOType.d0(get() * other)

operator fun IOType.D0.times(other: IOType.D0) = IOType.d0(get() * other.get())

operator fun IOType.D0.times(other: IOType.D1) = this.get() * other

operator fun IOType.D0.times(other: IOType.D2) = this.get() * other

operator fun IOType.D0.times(other: IOType.D3) = this.get() * other

/**
 * IOType.D1
 */
operator fun IOType.D1.times(other: Float): IOType.D1 = IOType.d1(shape) { this[it] * other }

operator fun IOType.D1.times(other: IOType.D1): IOType.D1 = IOType.d1(shape) { this[it] * other[it] }

/**
 * IOType.D2
 */
operator fun IOType.D2.times(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other }

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] * other[i, j] }

/**
 * IOType.D3
 */
operator fun IOType.D3.times(other: Float): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] * other }

operator fun IOType.D3.times(other: IOType.D3): IOType.D3 = IOType.d3(shape) { i, j, k ->
    this[i, j, k] * other[i, j, k]
}
