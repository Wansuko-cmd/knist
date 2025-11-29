package com.wsr.layer.process.conv

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.matmul.matMul
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
        val result = weight.toFilter() matMul input.toColumn()
        return Batch(input.size) { b ->
            IOType.d2(outputX, outputY) { f, o ->
                result[f, b * outputY + o]
            }
        }
    }

    private fun Batch<IOType.D2>.toColumn(): IOType.D2 {
        val row = kernel * channel
        val column = outputY * size
        val result = IOType.d2(row, column)

        for (batchIndex in 0 until size) {
            val input = this[batchIndex]
            for (rowIdx in 0 until row) {
                val channelIndex = rowIdx / kernel
                val kernelIndex = rowIdx % kernel
                for (colIdx in 0 until outputY) {
                    val columnIndex = batchIndex * outputY + colIdx
                    val inputIdx = colIdx * stride + kernelIndex - padding
                    if (inputIdx in 0 until inputSize) {
                        result[rowIdx, columnIndex] = input[channelIndex, inputIdx]
                    }
                }
            }
        }
        return result
    }

    private fun IOType.D3.toFilter(): IOType.D2 = IOType.d2(outputX, channel * kernel) { i, j ->
        val c = j / kernel
        val k = j % kernel
        this[i, c, k]
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        // forward
        val col = input.toColumn()
        val filterMatrix = weight.toFilter()
        val result = filterMatrix matMul col
        val output = Batch(input.size) { b ->
            IOType.d2(outputX, outputY) { f, o ->
                result[f, b * outputY + o]
            }
        }

        val delta = calcDelta(output)

        // dx (逆伝播)
        // reversed weight: [channel, filter, kernel] で kernel を反転
        val reversed = IOType.d2(channel * kernel, filter) { i, f ->
            val c = i / kernel
            val k = i % kernel
            weight[f, c, kernel - k - 1]
        }
        val deltaCol = delta.toDeltaColumn()
        val dxCol = reversed matMul deltaCol
        val dx = dxCol.toInputGradient(input)

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

    private fun Batch<IOType.D2>.toDeltaColumn(): IOType.D2 {
        val row = filter
        val column = outputY * size
        val result = IOType.d2(row, column)

        for (batchIndex in 0 until size) {
            val delta = this[batchIndex]
            for (f in 0 until filter) {
                for (o in 0 until outputY) {
                    val colIdx = batchIndex * outputY + o
                    result[f, colIdx] = delta[f, o]
                }
            }
        }
        return result
    }

    private fun IOType.D2.toInputGradient(originalInput: Batch<IOType.D2>): Batch<IOType.D2> {
        // dxCol: [channel * kernel, batchSize * outputY]
        // 逆畳み込みの結果を元の入力形状に戻す
        return Batch(originalInput.size) { b ->
            IOType.d2(channel, inputSize) { c, i ->
                var sum = 0f
                for (o in 0 until outputY) {
                    val k = i - o * stride + padding
                    if (k in 0 until kernel) {
                        val row = c * kernel + k
                        val col = b * outputY + o
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
