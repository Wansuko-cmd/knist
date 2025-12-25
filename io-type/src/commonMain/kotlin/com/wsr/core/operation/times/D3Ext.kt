package com.wsr.core.operation.times

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D3.times(other: Float): IOType.D3 {
    val result = Backend.times(x = value, y = other)
    return IOType.D3(shape = shape, value = result)
}

operator fun IOType.D3.times(other: IOType.D0): IOType.D3 {
    val result = Backend.times(x = value, y = other.get())
    return IOType.D3(shape = shape, value = result)
}

fun IOType.D3.times(other: IOType.D1, axis: Int): IOType.D3 {
    val result = Backend.times(
        x = value,
        xi = i,
        xj = j,
        xk = k,
        y = other.value,
        axis = axis,
    )
    return IOType.D3(shape = shape, value = result)
}

fun IOType.D3.times(other: IOType.D2, axis1: Int, axis2: Int): IOType.D3 {
    val result = Backend.times(
        x = value,
        xi = i,
        xj = j,
        xk = k,
        y = other.value,
        yi = other.i,
        yj = other.j,
        axis1 = axis1,
        axis2 = axis2,
    )
    return IOType.D3(shape = shape, value = result)
}

operator fun IOType.D3.times(other: IOType.D3): IOType.D3 {
    val result = Backend.times(x = value, y = other.value)
    return IOType.D3(shape = shape, value = result)
}
