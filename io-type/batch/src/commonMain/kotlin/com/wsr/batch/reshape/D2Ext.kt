package com.wsr.batch.reshape

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.reshape.broadcastToD3
import com.wsr.core.reshape.slice
import com.wsr.core.reshape.transpose
import com.wsr.core.set

fun Batch<IOType.D2>.transpose() = map { it.transpose() }

fun Batch<IOType.D2>.slice(i: IntRange = 0 until shape[0], j: IntRange = 0 until shape[1]) =
    map { it.slice(i = i, j = j) }

fun Batch<IOType.D2>.flatten() = Batch<IOType.D1>(
    shape = listOf(step),
    size = size,
    value = value,
)

fun Batch<IOType.D2>.toD3(): IOType.D3 = IOType.d3(listOf(size, shape[0], shape[1]), value)

fun IOType.D3.toBatch(): Batch<IOType.D2> = Batch(value = value, size = shape[0], shape = listOf(shape[1], shape[2]))

fun Batch<IOType.D2>.reshapeToD3(shape: List<Int>) = Batch<IOType.D3>(size = size, shape = shape, value = value)

fun Batch<IOType.D2>.reshapeToD3(i: Int, j: Int, k: Int) = reshapeToD3(listOf(i, j, k))

fun Batch<IOType.D2>.broadcastToD3(axis: Int, size: Int) = Batch(this.size) { this[it].broadcastToD3(axis, size) }

/**
 * Unfold: Batch<IOType.D2>を列形式に展開 (im2col)
 * 入力: [batchSize] x [channel, inputSize]
 * 出力: [windowSize * channel, outputSize * batchSize]
 */
fun Batch<IOType.D2>.unfold(windowSize: Int, stride: Int, padding: Int): IOType.D2 {
    val channel = shape[0]
    val inputSize = shape[1]
    val outputSize = (inputSize - windowSize + 2 * padding) / stride + 1
    val row = windowSize * channel
    val column = outputSize * size
    val result = IOType.d2(row, column)

    for (batchIndex in 0 until size) {
        val input = this[batchIndex]
        for (rowIdx in 0 until row) {
            val channelIndex = rowIdx / windowSize
            val windowIndex = rowIdx % windowSize
            for (colIdx in 0 until outputSize) {
                val columnIndex = batchIndex * outputSize + colIdx
                val inputIdx = colIdx * stride + windowIndex - padding
                if (inputIdx in 0 until inputSize) {
                    result[rowIdx, columnIndex] = input[channelIndex, inputIdx]
                }
            }
        }
    }
    return result
}

/**
 * Fold: 列形式をBatch<IOType.D2>に戻す (col2im)
 * 入力: [windowSize * channel, outputSize * batchSize]
 * 出力: [batchSize] x [channel, inputSize]
 * 注意: 重複部分は加算される
 */
fun IOType.D2.fold(batchSize: Int, channel: Int, inputSize: Int, stride: Int, padding: Int): Batch<IOType.D2> {
    val windowSize = shape[0] / channel
    val outputSize = shape[1] / batchSize
    return Batch(batchSize) { b ->
        IOType.d2(channel, inputSize) { c, i ->
            var sum = 0f
            for (outputIdx in 0 until outputSize) {
                val windowIndex = i - outputIdx * stride + padding
                if (windowIndex in 0 until windowSize) {
                    val row = c * windowSize + windowIndex
                    val col = b * outputSize + outputIdx
                    sum += this[row, col]
                }
            }
            sum
        }
    }
}
