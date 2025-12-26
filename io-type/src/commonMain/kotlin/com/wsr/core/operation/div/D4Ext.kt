package com.wsr.core.operation.div

import com.wsr.Backend
import com.wsr.core.IOType

operator fun IOType.D4.div(other: Float): IOType.D4 {
    val result = Backend.div(x = value, y = other)
    return IOType.D4(shape = shape, value = result)
}

operator fun IOType.D4.div(other: IOType.D4): IOType.D4 {
    val result = Backend.div(x = value, y = other.value)
    return IOType.D4(shape = shape, value = result)
}
