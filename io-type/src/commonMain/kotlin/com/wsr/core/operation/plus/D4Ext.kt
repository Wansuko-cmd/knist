package com.wsr.core.operation.plus

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.get

operator fun IOType.D4.plus(other: Float): IOType.D4 {
    val result = Backend.plus(x = value, y = other)
    return IOType.D4(shape = shape, value = result)
}

operator fun IOType.D4.plus(other: IOType.D4): IOType.D4 {
    val result = Backend.plus(x = value, y = other.value)
    return IOType.D4(shape = shape, value = result)
}
