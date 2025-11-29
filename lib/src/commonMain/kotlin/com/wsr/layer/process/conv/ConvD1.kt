package com.wsr.layer.process.conv

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.batch.reshape.toBatch
import com.wsr.batch.reshape.toD3
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.matmul.matMul
import com.wsr.core.reshape.transpose
import com.wsr.core.set
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class ConvD1 internal constructor(
    private val filter: Int,
    private val channel: Int,
    private val kernel: Int,
    private val stride: Int,
    private val padding: Int,
    private val inputSize: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D2() {
    override val outputX: Int = filter
    override val outputY: Int = (inputSize - kernel + 2 * padding) / stride + 1

    init {
        check((inputSize - kernel + 2 * padding) % stride == 0) {
            val output = (inputSize - kernel + 2 * padding) / stride.toFloat() + 1.0
            """
            invalid parameter.
            inputSize: $inputSize
            kernel: $kernel
            padding: $padding
            stride: $stride
            output: (inputSize - kernel + 2 * padding) % stride + 1 = $output
            """.trimIndent()
        }
    }

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val col = input.unfold(windowSize = kernel, stride = stride, padding = padding, inputSize = inputSize, outputSize = outputY)
        val result = weight.toFilter() matMul col
        // IOType.D2 [filter, batchSize * outputY] → Batch<IOType.D2> [batchSize] x [filter, outputY]
        // reshape to D3 [filter, batchSize, outputY] → transpose(1, 0, 2) → [batchSize, filter, outputY] → Batch<D2>
        return IOType.d3(listOf(filter, input.size, outputY), result.value).transpose(1, 0, 2).toBatch()
    }

    /**
     * Unfold: Batch<IOType.D2>を列形式に展開 (im2col)
     * 入力: [batchSize] x [channel, inputSize]
     * 出力: [windowSize * channel, outputSize * batchSize]
     */
    private fun Batch<IOType.D2>.unfold(
        windowSize: Int,
        stride: Int,
        padding: Int,
        inputSize: Int,
        outputSize: Int,
    ): IOType.D2 {
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

    // IOType.D3 [filter, channel, kernel] → IOType.D2 [filter, channel * kernel]
    private fun IOType.D3.toFilter(): IOType.D2 = IOType.d2(listOf(outputX, channel * kernel), value)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        // forward
        val col = input.unfold(windowSize = kernel, stride = stride, padding = padding, inputSize = inputSize, outputSize = outputY)
        val filterMatrix = weight.toFilter()
        val result = filterMatrix matMul col
        // IOType.D2 [filter, batchSize * outputY] → Batch<IOType.D2> [batchSize] x [filter, outputY]
        val output = IOType.d3(listOf(filter, input.size, outputY), result.value).transpose(1, 0, 2).toBatch()

        val delta = calcDelta(output)

        // dx (逆伝播)
        // reversed weight: [channel, filter, kernel] で kernel を反転
        val reversed = IOType.d2(channel * kernel, filter) { i, f ->
            val c = i / kernel
            val k = i % kernel
            weight[f, c, kernel - k - 1]
        }
        // Batch<IOType.D2> [batchSize] x [filter, outputY] → IOType.D2 [filter, batchSize * outputY]
        val deltaD3 = delta.toD3().transpose(1, 0, 2)
        val deltaCol = IOType.d2(listOf(filter, input.size * outputY), deltaD3.value)
        val dxCol = reversed matMul deltaCol
        val dx = dxCol.fold(batchSize = input.size, windowSize = kernel, stride = stride, padding = padding, inputSize = inputSize, outputSize = outputY)

        // dw (重み勾配)
        val dw = Batch(input.size) { b ->
            val deltaB = delta[b]
            val inputB = input[b]
            IOType.d3(filter, channel, kernel) { f, c, k ->
                var sum = 0f
                for (o in 0 until outputY) {
                    val inputIdx = o * stride + k - padding
                    if (inputIdx in 0 until inputSize) {
                        sum += deltaB[f, o] * inputB[c, inputIdx]
                    }
                }
                sum
            }
        }
        weight = optimizer.adapt(weight = weight, dw = dw)

        return dx
    }

    /**
     * Fold: 列形式をBatch<IOType.D2>に戻す (col2im)
     * 入力: [windowSize * channel, outputSize * batchSize]
     * 出力: [batchSize] x [channel, inputSize]
     * 注意: 重複部分は加算される
     */
    private fun IOType.D2.fold(
        batchSize: Int,
        windowSize: Int,
        stride: Int,
        padding: Int,
        inputSize: Int,
        outputSize: Int,
    ): Batch<IOType.D2> {
        return Batch(batchSize) { b ->
            IOType.d2(channel, inputSize) { c, i ->
                var sum = 0f
                for (o in 0 until outputSize) {
                    val windowIndex = i - o * stride + padding
                    if (windowIndex in 0 until windowSize) {
                        val row = c * windowSize + windowIndex
                        val col = b * outputSize + o
                        sum += this[row, col]
                    }
                }
                sum
            }
        }
    }
}

fun <T> NetworkBuilder.D2<T>.convD1(
    filter: Int,
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
) = addProcess(
    process =
    ConvD1(
        filter = filter,
        channel = inputX,
        kernel = kernel,
        stride = stride,
        padding = padding,
        inputSize = inputY,
        optimizer = optimizer.d3(filter, inputX, kernel),
        weight = initializer.d3(
            input = listOf(inputX, kernel),
            output = listOf(filter, kernel),
            x = filter,
            y = inputX,
            z = kernel,
        ),
    ),
)
