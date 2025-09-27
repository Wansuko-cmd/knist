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
    stride: Int = 1,
    padding: Int = 0,
): IOType.D1 {
    val filterSize = filter.shape[0]
    val input = this
        .addStridePadding(stride)
        .addPadding(filterSize - padding - 1)
    return input.convD1(filter, stride, padding)
}

private fun IOType.D1.addStridePadding(stride: Int) = IOType.d1(
    value = ArrayList<Double>().apply {
        for (i in 0 until shape[0] - 1) {
            add(value[i])
            addAll(Array(stride - 1) { 0.0 })
        }
        add(value[shape[0] - 1])
    }
)

private fun IOType.D1.addPadding(padding: Int) = IOType.d1(
    value = ArrayList<Double>().apply {
        addAll(Array(padding) { 0.0 })
        addAll(value)
        addAll(Array(padding) { 0.0 })
    },
)
