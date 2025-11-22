package com.wsr.func

import com.wsr.IOType
import com.wsr.collection.max
import com.wsr.collection.sum
import com.wsr.operator.div
import com.wsr.operator.minus
import kotlin.math.exp

fun IOType.D1.softmax(): IOType.D1 {
    val max = value.max()
    val exp = value.map { exp(it - max) }
    val sum = exp.sum()
    return IOType.d1(shape) { exp[it] / sum }
}

fun IOType.D2.softmax(): IOType.D2 {
    val max = value.max()
    val exp = value.map { exp(it - max) }
    val sum = exp.sum()
    return IOType.d2(shape) { x, y -> exp[x * shape[0] + y] / sum }
}

fun IOType.D2.softmax(axis: Int): IOType.D2 {
    val max = max(axis = axis)
    val exp = (this - max).exp()
    val sum = exp.sum(axis = axis)
    return exp / sum
}

fun IOType.D3.softmax(): IOType.D3 {
    val max = value.max()
    val exp = value.map { exp(it - max) }
    val sum = exp.sum()
    return IOType.d3(shape) { x, y, z -> exp[x * shape[0] * shape[1] + y * shape[1] + z] / sum }
}
