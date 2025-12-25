package com.wsr.core.operation.minus

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.operation.zip.zipWith

operator fun IOType.D1.minus(other: Float): IOType.D1 {
    val result = Backend.minus(x = value, y = other)
    return IOType.D1(value = result)
}

operator fun IOType.D1.minus(other: IOType.D0): IOType.D1 {
    val result = Backend.minus(x = value, y = other.get())
    return IOType.D1(value = result)
}

operator fun IOType.D1.minus(other: IOType.D1): IOType.D1 {
    val result = Backend.minus(x = value, y = other.value)
    return IOType.D1(value = result)
}

fun IOType.D1.minus(other: IOType.D2, axis: Int): IOType.D2 {
    val result = Backend.minus(x = value, y = other.value, yi = other.i, yj = other.j, axis = axis)
    return IOType.D2(shape = other.shape, value = result)
}
