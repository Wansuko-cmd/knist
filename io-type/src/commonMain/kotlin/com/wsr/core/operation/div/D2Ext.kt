package com.wsr.core.operation.div

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.get

operator fun IOType.D2.div(other: Float): IOType.D2 {
    val result = Backend.div(x = value, y = other)
    return IOType.D2(shape = shape, value = result)
}

operator fun IOType.D2.div(other: IOType.D0): IOType.D2 {
    val result = Backend.div(x = value, y = other.get())
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D2.div(other: IOType.D1, axis: Int): IOType.D2 {
    val result = Backend.div(x = value, xi = i, xj = j, y = other.value, axis = axis)
    return IOType.D2(shape = shape, value = result)
}

operator fun IOType.D2.div(other: IOType.D2): IOType.D2 {
    val result = Backend.div(x = value, y = other.value)
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D2.div(other: IOType.D3, axis1: Int, axis2: Int): IOType.D3 {
    val result = Backend.div(
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
