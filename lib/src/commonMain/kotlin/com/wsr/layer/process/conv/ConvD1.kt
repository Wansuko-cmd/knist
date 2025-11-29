package com.wsr.layer.process.conv

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.batch.reshape.convert.toBatch
import com.wsr.batch.reshape.convert.toD3
import com.wsr.batch.reshape.fold.fold
import com.wsr.batch.reshape.fold.unfold
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.matmul.matMul
import com.wsr.core.reshape.reshape.reshapeToD2
import com.wsr.core.reshape.reshape.reshapeToD3
import com.wsr.core.reshape.transpose.transpose
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
        val col = input.unfold(windowSize = kernel, stride = stride, padding = padding)
        return (weight.reshapeToD2(outputX, channel * kernel) matMul col)
            .reshapeToD3(i = filter, j = input.size, k = outputY)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .toBatch()
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val col = input.unfold(windowSize = kernel, stride = stride, padding = padding)
        val output = (weight.reshapeToD2(i = outputX, j = channel * kernel) matMul col)
            .reshapeToD3(i = filter, j = input.size, k = outputY)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .toBatch()

        val delta = calcDelta(output)

        val reversed = IOType.d2(i = channel * kernel, j = filter) { i, f ->
            val c = i / kernel
            val k = i % kernel
            weight[f, c, kernel - k - 1]
        }
        val deltaCol = delta.toD3()
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = filter, j = input.size * outputY)
        val dx = (reversed matMul deltaCol).fold(
            channel = channel,
            batchSize = input.size,
            inputSize = inputSize,
            stride = stride,
            padding = padding,
        )

        // dw (重み勾配)
        val dw = Batch(input.size) { b ->
            val delta = delta[b]
            val input = input[b]
            IOType.d3(filter, channel, kernel) { f, c, k ->
                var sum = 0f
                for (o in 0 until outputY) {
                    val inputIdx = o * stride + k - padding
                    if (inputIdx in 0 until inputSize) {
                        sum += delta[f, o] * input[c, inputIdx]
                    }
                }
                sum
            }
        }
        weight = optimizer.adapt(weight = weight, dw = dw)

        return dx
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
