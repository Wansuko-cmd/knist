package com.wsr.batch.reshape.fold

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.set

/**
 * Unfold: Batch<IOType.D3>を列形式に展開 (im2col)
 * 入力: [batchSize] x [channel, inputSizeX, inputSizeY]
 * 出力: [windowSize * windowSize * channel, outputSizeX * outputSizeY * batchSize]
 */
fun Batch<IOType.D3>.unfold(windowSize: Int, stride: Int, padding: Int): IOType.D2 {
    val channel = shape[0]
    val inputX = shape[1]
    val inputY = shape[2]
    val outputX = (inputX - windowSize + 2 * padding) / stride + 1
    val outputY = (inputY - windowSize + 2 * padding) / stride + 1
    val row = windowSize * windowSize * channel
    val column = outputX * outputY * size
    val result = IOType.d2(row, column)

    for (batchIndex in 0 until size) {
        val input = this[batchIndex]
        for (c in 0 until channel) {
            for (wy in 0 until windowSize) {
                for (wx in 0 until windowSize) {
                    val rowIdx = c * windowSize * windowSize + wy * windowSize + wx
                    for (oy in 0 until outputY) {
                        for (ox in 0 until outputX) {
                            val columnIndex = batchIndex * outputX * outputY + oy * outputX + ox
                            val inputIdxX = ox * stride + wx - padding
                            val inputIdxY = oy * stride + wy - padding
                            if (inputIdxX in 0 until inputX && inputIdxY in 0 until inputY) {
                                result[rowIdx, columnIndex] = input[c, inputIdxX, inputIdxY]
                            }
                        }
                    }
                }
            }
        }
    }
    return result
}

/**
 * Fold: 列形式をBatch<IOType.D3>に戻す (col2im)
 * 入力: [windowSize * windowSize * channel, outputSizeX * outputSizeY * batchSize]
 * 出力: [batchSize] x [channel, inputSizeX, inputSizeY]
 * 注意: 重複部分は加算される
 */
fun IOType.D2.fold(
    batchSize: Int,
    channel: Int,
    inputX: Int,
    inputY: Int,
    stride: Int,
    padding: Int,
): Batch<IOType.D3> {
    val windowSize = kotlin.math.sqrt((shape[0] / channel).toDouble()).toInt()
    val outputSizeXY = shape[1] / batchSize
    val outputSizeX = kotlin.math.sqrt(outputSizeXY.toDouble()).toInt()
    val outputSizeY = outputSizeXY / outputSizeX

    return Batch(batchSize) { b ->
        IOType.d3(channel, inputX, inputY) { c, ix, iy ->
            var sum = 0f
            for (oy in 0 until outputSizeY) {
                for (ox in 0 until outputSizeX) {
                    val windowIdxX = ix - ox * stride + padding
                    val windowIdxY = iy - oy * stride + padding
                    if (windowIdxX in 0 until windowSize && windowIdxY in 0 until windowSize) {
                        val row = c * windowSize * windowSize + windowIdxY * windowSize + windowIdxX
                        val col = b * outputSizeX * outputSizeY + oy * outputSizeX + ox
                        sum += this[row, col]
                    }
                }
            }
            sum
        }
    }
}
