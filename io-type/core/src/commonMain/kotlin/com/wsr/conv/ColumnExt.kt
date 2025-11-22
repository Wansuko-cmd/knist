package com.wsr.conv

import com.wsr.IOType
import com.wsr.get

fun List<IOType.D2>.toColumn(kernel: Int, stride: Int = 1, padding: Int = 0): Array<FloatArray> {
    val (channel, inputSize) = first().shape
    val output = (inputSize - kernel + 2 * padding) / stride + 1
    val result = Array(this.size * output) { FloatArray(kernel * channel) }
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
