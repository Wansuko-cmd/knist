package com.wsr.core.collection.average

import com.wsr.core.IOType
import com.wsr.core.collection.sum.sum
import com.wsr.core.operation.div.div

fun IOType.D1.average(): Float = sum() / shape[0]

fun IOType.D2.average(): Float = sum() / (shape[0] * shape[1])

fun IOType.D2.average(axis: Int): IOType.D1 = when (axis) {
    0, 1 -> sum(axis = axis) / shape[axis].toFloat()
    else -> throw IllegalArgumentException("IOType.D2.max axis is $axis not 0 or 1.")
}

fun IOType.D3.average(): Float = sum() / (shape[0] * shape[1] * shape[2])

fun IOType.D3.average(axis: Int) = when (axis) {
    0, 1, 2 -> sum(axis = axis) / shape[axis].toFloat()
    else -> throw IllegalArgumentException("IOType.D3.max axis is $axis not 0, 1 or 2.")
}
