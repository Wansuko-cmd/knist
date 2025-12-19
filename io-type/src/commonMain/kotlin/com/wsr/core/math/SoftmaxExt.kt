package com.wsr.core.math

import com.wsr.core.IOType
import com.wsr.core.collection.max.max
import com.wsr.core.collection.sum.sum
import com.wsr.core.operation.div.div
import com.wsr.core.operation.div.div2
import com.wsr.core.operation.minus.minus
import com.wsr.core.operation.minus.minus2

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
    val exp = this.minus2(other = max, axis = if (axis == 0) 1 else 0).exp()
    val sum = exp.sum(axis = axis)
    return exp.div2(other = sum, axis = if (axis == 0) 1 else 0)
}

fun IOType.D3.softmax(): IOType.D3 {
    val max = max()
    val exp = (this - max).exp()
    val sum = exp.sum()
    return exp / sum
}

fun IOType.D3.softmax(axis: Int): IOType.D3 {
    val axis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    val axis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }
    val max = max(axis = axis)
    val exp = this.minus2(other = max, axis1 = axis1, axis2 = axis2).exp()
    val sum = exp.sum(axis = axis)
    return exp.div2(other = sum, axis1 = axis1, axis2 = axis2)
}
