package com.wsr.d1

import com.wsr.IOType

fun IOType.D1.convD1(
    filter: IOType.D1,
    stride: Int = 1,
    padding: Int = 0,
): IOType.D1 {
    val inputSize = shape[0]
    val filterSize = filter.shape[0]
    val outputSize = (inputSize - filterSize + 2 * padding) / stride + 1
    val inputWithPadding = this.addPadding(padding)
    return IOType.d1(outputSize) {
        var sum = 0.0
        for (k in 0 until filterSize) {
            sum += inputWithPadding[it * stride + k] * filter[k]
        }
        sum
    }
}

fun IOType.D1.deConvD1(
    filter: IOType.D1,
    stride: Int = 1, // TODO
    padding: Int = 0,
): IOType.D1 {
    val filterSize = filter.shape[0]
    val inputWithPadding = this.addPadding(filterSize - padding - 1)
    return inputWithPadding.convD1(filter, stride, padding)
}

private fun IOType.D1.addPadding(padding: Int) = IOType.d1(
    value = ArrayList<Double>().apply {
        addAll(Array(padding) { 0.0 })
        addAll(value)
        addAll(Array(padding) { 0.0 })
    },
)
