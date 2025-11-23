package com.wsr.core.operation.times

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get

operator fun IOType.D3.times(other: Float): IOType.D3 = IOType.d3(shape) { i, j, k -> this[i, j, k] * other }

operator fun IOType.D3.times(other: IOType.D3): IOType.D3 = IOType.d3(shape) { i, j, k ->
    this[i, j, k] * other[i, j, k]
}