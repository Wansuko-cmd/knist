package com.wsr.conv

import com.wsr.IOType
import com.wsr.reshape.toD2

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
    return input.convD1(filter, stride = 1, padding = 0)
}

private fun IOType.D1.addStridePadding(stride: Int) = IOType.d1(
    value = ArrayList<Double>().apply {
        for (i in 0 until shape[0] - 1) {
            add(value[i])
            addAll(Array(stride - 1) { 0.0 })
        }
        add(value[shape[0] - 1])
    },
)

private fun IOType.D1.addPadding(padding: Int) = IOType.d1(
    value = ArrayList<Double>().apply {
        addAll(Array(padding) { 0.0 })
        addAll(value)
        addAll(Array(padding) { 0.0 })
    },
)

/**
 * バッチ対応版
 */
fun List<IOType.D2>.convD1(
    weight: IOType.D3,
    stride: Int = 1,
    padding: Int = 0,
): List<IOType.D2> {
    val (outputX, _, kernel) = weight.shape
    val outputY = (first().shape[1] - kernel + 2 * padding) / stride + 1

    val col = this.toColumn(kernel, stride, padding)
    val filter = weight.toFilter()

    val result = col dot filter

    return List(size) { b ->
        IOType.d2(outputX, outputY) { f, o ->
            result[f][b * outputY + o]
        }
    }
}

fun List<IOType.D2>.deConvD1(
    weight: IOType.D3,
    stride: Int = 1,
    padding: Int = 0,
): List<IOType.D2> {
    val filterSize = weight.shape[2]
    val input = this
        .addStridePadding(stride)
        .addPadding(filterSize - padding - 1)
    return input.convD1(weight = weight, stride = 1, padding = 0)
}

private fun List<IOType.D2>.addStridePadding(stride: Int) = map { io ->
    (0 until io.shape[0]).map { io[it].addStridePadding(stride) }.toD2()
}

private fun List<IOType.D2>.addPadding(padding: Int) = map { io ->
    (0 until io.shape[0]).map { io[it].addPadding(padding) }.toD2()
}
