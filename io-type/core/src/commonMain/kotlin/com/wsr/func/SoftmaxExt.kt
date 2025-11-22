package com.wsr.func

import com.wsr.IOType
import com.wsr.collection.max
import com.wsr.collection.sum
import com.wsr.operator.div
import com.wsr.operator.minus

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
    val exp = (this - max).exp()
    val sum = exp.sum(axis = axis)
    return exp / sum
}

fun IOType.D3.softmax(): IOType.D3 {
    val max = max()
    val exp = (this - max).exp()
    val sum = exp.sum()
    return exp / sum
}
