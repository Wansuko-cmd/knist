package com.wsr.power

import com.wsr.IOType

fun IOType.D1.sqrt(e: Double = 1e-7): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = kotlin.math.sqrt(result[i] + e)
    }
    return IOType.d1(result)
}

fun IOType.D2.sqrt(e: Double = 1e-7): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = kotlin.math.sqrt(result[i] + e)
    }
    return IOType.d2(shape, result)
}

fun IOType.D3.sqrt(e: Double = 1e-7): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = kotlin.math.sqrt(result[i] + e)
    }
    return IOType.d3(shape, result)
}
