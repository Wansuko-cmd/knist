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
operator fun Float.div(other: IOType.D0) = IOType.d0(this / other.get())

operator fun Float.div(other: IOType.D1) = IOType.d1(other.shape) { i -> this / other[i] }

operator fun Float.div(other: IOType.D2) = IOType.d2(other.shape) { i, j -> this / other[i, j] }

operator fun Float.div(other: IOType.D3) = IOType.d3(other.shape) { i, j, k -> this / other[i, j, k] }

/**
 * IOType.D0
 */
operator fun IOType.D0.div(other: Float) = IOType.d0(get() / other)

/**
 * IOType.D1
 */
operator fun IOType.D1.div(other: Float): IOType.D1 = IOType.d1(shape) { this[it] / other }

operator fun IOType.D1.div(other: IOType.D0): IOType.D1 = IOType.d1(shape) { this[it] / other.get() }

operator fun IOType.D1.div(other: IOType.D1): IOType.D1 = IOType.d1(this.shape) { i -> this[i] / other[i] }

/**
 * IOType.D2
 */
operator fun IOType.D2.div(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] / other }

operator fun IOType.D2.div(other: IOType.D0): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] / other.get() }

operator fun IOType.D2.div(other: IOType.D1): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] / other[i] }

operator fun IOType.D2.div(other: IOType.D2): IOType.D2 = IOType.d2(this.shape) { i, j -> this[i, j] / other[i, j] }

/**
 * IOType.D3
 */
operator fun IOType.D3.div(other: Float): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] / other }

operator fun IOType.D3.div(other: IOType.D0): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] / other.get() }

operator fun IOType.D3.div(other: IOType.D3): IOType.D3 = IOType.d3(this.shape) { i, j, k ->
    this[i, j, k] /
        other[i, j, k]
}
