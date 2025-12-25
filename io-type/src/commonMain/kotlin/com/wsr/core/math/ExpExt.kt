package com.wsr.core.math

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get

fun IOType.D1.exp(): IOType.D1 {
    val result = Backend.exp(x = value)
    return IOType.D1(value = result)
}

fun IOType.D2.exp(): IOType.D2 {
    val result = Backend.exp(x = value)
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D3.exp(): IOType.D3 {
    val result = Backend.exp(x = value)
    return IOType.D3(shape = shape, value = result)
}
