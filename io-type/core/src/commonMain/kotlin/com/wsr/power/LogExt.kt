package com.wsr.power

import com.wsr.IOType

fun IOType.D1.ln(e: Float): IOType.D1 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] = kotlin.math.ln(result[i] + e)
    return IOType.d1(result)
}

@JvmName("lnD1s")
fun List<IOType.D1>.ln(e: Float): List<IOType.D1> = map { it.ln(e) }

fun IOType.D2.ln(e: Float): IOType.D2 {
    val result = this.value.copyOf()
    for (i in result.indices) result[i] = kotlin.math.ln(result[i] + e)
    return IOType.d2(shape, result)
}

@JvmName("lnD2s")
fun List<IOType.D2>.ln(e: Float): List<IOType.D2> = map { it.ln(e) }
