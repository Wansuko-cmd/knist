package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Float
 */
operator fun Float.plus(other: IOType.D1): IOType.D1 = IOType.d1(other.shape) { this + other[it] }

operator fun Float.plus(other: IOType.D2): IOType.D2 = IOType.d2(other.shape) { i, j -> this + other[i, j] }

operator fun Float.plus(other: IOType.D3): IOType.D3 = IOType.d3(other.shape) { i, j, k -> this + other[i, j, k] }

/**
 * IOType.D1
 */
operator fun IOType.D1.plus(other: Float): IOType.D1 = IOType.d1(shape) { this[it] + other }

operator fun IOType.D1.plus(other: IOType.D1): IOType.D1 = IOType.d1(shape) { this[it] + other[it] }

/**
 * IOType.D2
 */
operator fun IOType.D2.plus(other: Float): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other }

operator fun IOType.D2.plus(other: IOType.D2): IOType.D2 = IOType.d2(shape) { i, j -> this[i, j] + other[i, j] }

/**
 * IOType.D3
 */
operator fun IOType.D3.plus(other: Float): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] + other }

operator fun IOType.D3.plus(other: IOType.D3): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] + other[i, j, k] }
