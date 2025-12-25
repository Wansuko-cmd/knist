package com.wsr.core.operation.minus

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d4
import com.wsr.core.get

operator fun IOType.D4.minus(other: Float): IOType.D4 {
    val result = Backend.minus(x = value, y = other)
    return IOType.D4(shape = shape, value = result)
}

operator fun IOType.D4.minus(other: IOType.D4): IOType.D4 {
    val result = Backend.minus(x = value, y = other.value)
    return IOType.D4(shape = shape, value = result)
}
