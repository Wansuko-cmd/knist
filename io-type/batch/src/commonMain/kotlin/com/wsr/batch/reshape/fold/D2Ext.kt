package com.wsr.batch.reshape.fold

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.set

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
