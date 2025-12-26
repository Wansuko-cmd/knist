package com.wsr.core.operation.times

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.get

operator fun Float.times(other: IOType.D0): IOType.D0 = IOType.d0(this * other.get())

operator fun Float.times(other: IOType.D1): IOType.D1 {
    val result = Backend.times(x = this, y = other.value)
    return IOType.D1(value = result)
}

operator fun Float.times(other: IOType.D2): IOType.D2 {
    val result = Backend.times(x = this, y = other.value)
    return IOType.D2(shape = other.shape, value = result)
}

operator fun Float.times(other: IOType.D3): IOType.D3 {
    val result = Backend.times(x = this, y = other.value)
    return IOType.D3(shape = other.shape, value = result)
}

operator fun Float.times(other: IOType.D4): IOType.D4 {
    val result = Backend.times(x = this, y = other.value)
    return IOType.D4(shape = other.shape, value = result)
}

operator fun IOType.D0.times(other: Float): IOType.D0 = IOType.d0(get() * other)

operator fun IOType.D0.times(other: IOType.D0): IOType.D0 = IOType.d0(get() * other.get())

operator fun IOType.D0.times(other: IOType.D1): IOType.D1 {
    val result = Backend.times(x = get(), y = other.value)
    return IOType.D1(value = result)
}

operator fun IOType.D0.times(other: IOType.D2): IOType.D2 {
    val result = Backend.times(x = get(), y = other.value)
    return IOType.D2(shape = other.shape, value = result)
}

operator fun IOType.D0.times(other: IOType.D3): IOType.D3 {
    val result = Backend.times(x = get(), y = other.value)
    return IOType.D3(shape = other.shape, value = result)
}

operator fun IOType.D0.times(other: IOType.D4): IOType.D4 {
    val result = Backend.times(x = get(), y = other.value)
    return IOType.D4(shape = other.shape, value = result)
}
