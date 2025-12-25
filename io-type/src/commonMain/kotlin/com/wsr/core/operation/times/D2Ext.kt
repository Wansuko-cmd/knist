package com.wsr.core.operation.times

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith
operator fun IOType.D2.times(other: Float): IOType.D2 {
    val result = Backend.times(x = value, y = other)
    return IOType.D2(shape = shape, value = result)
}

operator fun IOType.D2.times(other: IOType.D0): IOType.D2 {
    val result = Backend.times(x = value, y = other.get())
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D2.times(other: IOType.D1, axis: Int): IOType.D2 {
    val result = Backend.times(x = value, xi = i, xj = j, y = other.value, axis = axis)
    return IOType.D2(shape = shape, value = result)
}

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 {
    val result = Backend.times(x = value, y = other.value)
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D2.times(other: IOType.D3, axis1: Int, axis2: Int): IOType.D3 {
    val result = Backend.times(
        x = value,
        xi = i,
        xj = j,
        y = other.value,
        yi = other.i,
        yj = other.j,
        yk = other.k,
        axis1 = axis1,
        axis2 = axis2,
    )
    return IOType.D3(shape = other.shape, value = result)
}
