package com.wsr.core.math

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.get

fun IOType.D1.ln(e: Float): IOType.D1 {
    val result = Backend.ln(x = value, e = e)
    return IOType.D1(value = result)
}

fun IOType.D2.ln(e: Float): IOType.D2 {
    val result = Backend.ln(x = value, e = e)
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D3.ln(e: Float): IOType.D3 {
    val result = Backend.ln(x = value, e = e)
    return IOType.D3(shape = shape, value = result)
}
