package com.wsr.power

import com.wsr.IOType
import kotlin.math.sqrt

fun IOType.D1.sqrt(): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = sqrt(result[i])
    }
    return IOType.d1(result)
}

fun IOType.D2.sqrt(): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = sqrt(result[i])
    }
    return IOType.d2(shape, result)
}

fun IOType.D3.sqrt(): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = sqrt(result[i])
    }
    return IOType.d3(shape, result)
}
