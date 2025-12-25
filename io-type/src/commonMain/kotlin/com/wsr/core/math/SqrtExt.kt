package com.wsr.core.math

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.d0
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.core.get
fun IOType.D0.sqrt(e: Float = 1e-7f): IOType.D0 {
    val result = Backend.sqrt(x = value, e = e)
    return IOType.D0(value = result)
}

fun IOType.D1.sqrt(e: Float = 1e-7f): IOType.D1 {
    val result = Backend.sqrt(x = value, e = e)
    return IOType.D1(value = result)
}

fun IOType.D2.sqrt(e: Float = 1e-7f): IOType.D2 {
    val result = Backend.sqrt(x = value, e = e)
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D3.sqrt(e: Float = 1e-7f): IOType.D3 {
    val result = Backend.sqrt(x = value, e = e)
    return IOType.D3(shape = shape, value = result)
}

fun IOType.D4.sqrt(e: Float = 1e-7f): IOType.D4 {
    val result = Backend.sqrt(x = value, e = e)
    return IOType.D4(shape = shape, value = result)
}
