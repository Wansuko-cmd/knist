package com.wsr.core.operation.times

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d4
import com.wsr.core.get

operator fun IOType.D4.times(other: Float): IOType.D4 {
    val result = Backend.times(x = value, y = other)
    return IOType.D4(shape = shape, value = result)
}

operator fun IOType.D4.times(other: IOType.D4): IOType.D4 {
    val result = Backend.times(x = value, y = other.value)
    return IOType.D4(shape = shape, value = result)
}
