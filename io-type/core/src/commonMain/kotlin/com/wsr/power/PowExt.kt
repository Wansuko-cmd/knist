package com.wsr.power

import com.wsr.IOType
import kotlin.math.pow

fun IOType.D0.pow(n: Int) = IOType.d0(get().pow(n))

fun IOType.D1.pow(n: Int): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = result[i].pow(n)
    }
    return IOType.d1(result)
}

@JvmName("powD1s")
fun List<IOType.D1>.pow(n: Int) = map { it.pow(n) }

fun IOType.D2.pow(n: Int): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = result[i].pow(n)
    }
    return IOType.d2(shape, result)
}

@JvmName("powD2s")
fun List<IOType.D2>.pow(n: Int): List<IOType.D2> = map { it.pow(n) }

fun IOType.D3.pow(n: Int): IOType.D3 {
    val result = this.value.copyOf()
    for (i in result.indices) {
        result[i] = result[i].pow(n)
    }
    return IOType.d3(shape, result)
}

@JvmName("powD3s")
fun List<IOType.D3>.pow(n: Int) = map { it.pow(n) }
