package com.wsr.pow

import com.wsr.IOType
import kotlin.math.pow

fun IOType.D1.pow(n: Int): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = result[i].pow(n)
    }
    return IOType.d1(result)
}

fun IOType.D2.pow(n: Int): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = result[i].pow(n)
    }
    return IOType.d2(shape, result)
}

fun IOType.D3.pow(n: Int): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = result[i].pow(n)
    }
    return IOType.d3(shape, result)
}
