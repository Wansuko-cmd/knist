package com.wsr.conv

import com.wsr.IOType

fun List<IOType.D2>.convD1(
    filter: IOType.D3,
    stride: Int = 1,
    padding: Int = 0,
): List<IOType.D2> {
    val (outputX, _, kernel) = filter.shape
    val outputY = (first().shape[1] - kernel + 2 * padding) / stride + 1
    val col = im2col(kernel, stride, padding)
    val filter = filter.flatten()
    val result = Array(filter.size) { DoubleArray(col.size) }
    for (f in filter.indices) {
        for (i in col.indices) {
            result[f][i] = col[i] dot filter[f]
        }
    }
    return List(size) { b ->
        IOType.d2(outputX, outputY) { f, o ->
            result[f][b * outputY + o]
        }
    }
}

private fun List<IOType.D2>.im2col(
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
): Array<DoubleArray> {
    val (channel, inputSize) = first().shape
    val output = (inputSize - kernel + 2 * padding) / stride + 1
    val result = Array(this.size * output) { DoubleArray(kernel * channel) }
    this.forEachIndexed { index, ioType ->
        for (o in 0 until output) {
            val row = index * output + o
            for (k in 0 until kernel) {
                val inputIndex = o * stride + k - padding
                if (inputIndex in 0 until inputSize) {
                    for (c in 0 until channel) {
                        result[row][c * kernel + k] = ioType[c, inputIndex]
                    }
                }
            }
        }
    }
    return result
}

private fun IOType.D3.flatten(): Array<DoubleArray> {
    val filterCount = shape[0]
    val channels = shape[1]
    val kernel = shape[2]

    return Array(filterCount) { f ->
        DoubleArray(channels * kernel) { i ->
            val c = i / kernel
            val k = i % kernel
            this[f, c, k]
        }
    }
}

private infix fun DoubleArray.dot(other: DoubleArray): Double {
    var sum = 0.0
    for (i in indices) {
        sum += this[i] * other[i]
    }
    return sum
}

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
