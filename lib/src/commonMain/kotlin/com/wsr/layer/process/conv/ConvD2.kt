package com.wsr.layer.process.conv

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.batch.reshape.convert.toBatch
import com.wsr.batch.reshape.convert.toD4
import com.wsr.batch.reshape.fold.fold
import com.wsr.batch.reshape.fold.unfold
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d4
import com.wsr.core.get
import com.wsr.core.operation.matmul.matMul
import com.wsr.core.reshape.reshape.reshapeToD2
import com.wsr.core.reshape.reshape.reshapeToD4
import com.wsr.core.reshape.transpose.transpose
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class ConvD2 internal constructor(
    private val filter: Int,
    private val channel: Int,
    private val kernel: Int,
    private val stride: Int,
    private val padding: Int,
    private val inputX: Int,
    private val inputY: Int,
    private val optimizer: Optimizer.D4,
    private var weight: IOType.D4,
) : Process.D3() {
    override val outputX: Int = filter
    override val outputY: Int = (inputX - kernel + 2 * padding) / stride + 1
    override val outputZ: Int = (inputY - kernel + 2 * padding) / stride + 1
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        val col = input.unfold(windowSize = kernel, stride = stride, padding = padding)
        return (weight.reshapeToD2(outputX, channel * kernel * kernel) matMul col)
            .reshapeToD4(i = filter, j = input.size, k = outputY, l = outputZ)
            .transpose(axisI = 1, axisJ = 0, axisK = 2, axisL = 3)
            .toBatch()
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val col = input.unfold(windowSize = kernel, stride = stride, padding = padding)
        val output = (weight.reshapeToD2(i = outputX, j = channel * kernel * kernel) matMul col)
            .reshapeToD4(i = filter, j = input.size, k = outputY, l = outputZ)
            .transpose(axisI = 1, axisJ = 0, axisK = 2, axisL = 3)
            .toBatch()

        val delta = calcDelta(output)

        val reversed = IOType.d2(i = channel * kernel * kernel, j = filter) { i, f ->
            val c = i / (kernel * kernel)
            val ky = (i % (kernel * kernel)) / kernel
            val kx = i % kernel
            weight[f, c, kernel - ky - 1, kernel - kx - 1]
        }
        val deltaCol = delta.toD4()
            .transpose(axisI = 1, axisJ = 0, axisK = 2, axisL = 3)
            .reshapeToD2(i = filter, j = input.size * outputY * outputZ)
        val dx = (reversed matMul deltaCol).fold(
            batchSize = input.size,
            channel = channel,
            inputX = inputX,
            inputY = inputY,
            stride = stride,
            padding = padding,
        )

        // dw (重み勾配)
        val dw = Batch(input.size) { b ->
            val delta = delta[b]
            val input = input[b]
            IOType.d4(filter, channel, kernel, kernel) { f, c, ky, kx ->
                var sum = 0f
                for (oy in 0 until outputY) {
                    for (ox in 0 until outputZ) {
                        val inputIdxX = ox * stride + kx - padding
                        val inputIdxY = oy * stride + ky - padding
                        if (inputIdxX in 0 until inputX && inputIdxY in 0 until inputY) {
                            sum += delta[f, oy, ox] * input[c, inputIdxX, inputIdxY]
                        }
                    }
                }
                sum
            }
        }
        weight = optimizer.adapt(weight = weight, dw = dw)

        return dx
    }
}

fun <T> NetworkBuilder.D3<T>.convD2(
    filter: Int,
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
) = addProcess(
    process =
    ConvD2(
        filter = filter,
        channel = inputX,
        kernel = kernel,
        stride = stride,
        padding = padding,
        inputX = inputY,
        inputY = inputZ,
        optimizer = optimizer.d4(filter, inputX, kernel, kernel),
        weight = initializer.d4(
            input = listOf(inputX, kernel, kernel),
            output = listOf(filter, kernel, kernel),
            i = filter,
            j = inputX,
            k = kernel,
            l = kernel,
        ),
    ),
)
