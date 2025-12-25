package com.wsr.core.operation.minus

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.get

operator fun Float.minus(other: IOType.D0): IOType.D0 = IOType.d0(value = this - other.get())

operator fun Float.minus(other: IOType.D1): IOType.D1 {
    val result = Backend.minus(x = this, y = other.value)
    return IOType.D1(value = result)
}

operator fun Float.minus(other: IOType.D2): IOType.D2 {
    val result = Backend.minus(x = this, y = other.value)
    return IOType.D2(shape = other.shape, value = result)
}

operator fun Float.minus(other: IOType.D3): IOType.D3 {
    val result = Backend.minus(x = this, y = other.value)
    return IOType.D3(shape = other.shape, value = result)
}

operator fun Float.minus(other: IOType.D4): IOType.D4 {
    val result = Backend.minus(x = this, y = other.value)
    return IOType.D4(shape = other.shape, value = result)
}

operator fun IOType.D0.minus(other: Float): IOType.D0 = IOType.d0(value = get() - other)

operator fun IOType.D0.minus(other: IOType.D0): IOType.D0 = IOType.d0(get() - other.get())

operator fun IOType.D0.minus(other: IOType.D1): IOType.D1 {
    val result = Backend.minus(x = get(), y = other.value)
    return IOType.D1(value = result)
}

operator fun IOType.D0.minus(other: IOType.D2): IOType.D2 {
    val result = Backend.minus(x = get(), y = other.value)
    return IOType.D2(shape = other.shape, value = result)
}

operator fun IOType.D0.minus(other: IOType.D3): IOType.D3 {
    val result = Backend.minus(x = get(), y = other.value)
    return IOType.D3(shape = other.shape, value = result)
}

operator fun IOType.D0.minus(other: IOType.D4): IOType.D4 {
    val result = Backend.minus(x = get(), y = other.value)
    return IOType.D4(shape = other.shape, value = result)
}
