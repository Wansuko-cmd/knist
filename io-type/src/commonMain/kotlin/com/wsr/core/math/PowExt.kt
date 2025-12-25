package com.wsr.core.math

import com.wsr.Backend
import com.wsr.core.IOType
import com.wsr.core.get
import kotlin.math.pow

fun IOType.D0.pow(n: Int): IOType.D0 {
    val result = Backend.pow(x = value, n = n)
    return IOType.D0(value = result)
}

fun IOType.D1.pow(n: Int): IOType.D1 {
    val result = Backend.pow(x = value, n = n)
    return IOType.D1(value = result)
}

fun IOType.D2.pow(n: Int): IOType.D2 {
    val result = Backend.pow(x = value, n = n)
    return IOType.D2(shape = shape, value = result)
}

fun IOType.D3.pow(n: Int): IOType.D3 {
    val result = Backend.pow(x = value, n = n)
    return IOType.D3(shape = shape, value = result)
}

fun IOType.D4.pow(n: Int): IOType.D4 {
    val result = Backend.pow(x = value, n = n)
    return IOType.D4(shape = shape, value = result)
}
