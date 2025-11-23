package com.wsr.core.math

import com.wsr.core.IOType
import com.wsr.core.collection.max.max
import com.wsr.core.collection.sum.sum
import com.wsr.core.operation.div.div
import com.wsr.core.operation.minus.minus

fun IOType.D1.softmax(): IOType.D1 {
    val max = max()
    val exp = (this - max).exp()
    val sum = exp.sum()
    return exp / sum
}

fun IOType.D2.softmax(): IOType.D2 {
    val max = max()
    val exp = (this - max).exp()
    val sum = exp.sum()
    return exp / sum
}

fun IOType.D2.softmax(axis: Int): IOType.D2 {
    val max = max(axis = axis)
    val exp = this.minus(other = max, axis = axis).exp()
    val sum = exp.sum(axis = axis)
    return exp.div(other = sum, axis = axis)
}

fun IOType.D3.softmax(): IOType.D3 {
    val max = max()
    val exp = (this - max).exp()
    val sum = exp.sum()
    return exp / sum
}

fun IOType.D3.softmax(axis: Int): IOType.D3 {
    val max = max(axis = axis)
    val exp = this.minus(other = max, axis = axis).exp()
    val sum = exp.sum(axis = axis)
    return exp.div(other = sum, axis = axis)
}
