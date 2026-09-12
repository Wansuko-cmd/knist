package com.wsr.knist.network.process.compute.conv

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.reshapeToD3
import com.wsr.knist.batch.shape.reshapeToD4
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD4
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.shape.reshapeToD2
import com.wsr.knist.core.shape.reshapeToD4
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class ConvD2 internal constructor(
    private val filter: Int,
    private val channel: Int,
    private val kernel: Int,
    private val stride: Int,
    private val dilation: Int,
    private val padding: Int,
    private val height: Int,
    private val width: Int,
    private var optimizer: Optimizer.D4,
    private var weight: IOType.D4.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D3() {
    override val inputI: Int = channel
    override val inputJ: Int = height
    override val inputK: Int = width

    override val outputI: Int = filter
    private val kernelSize = (kernel - 1) * dilation + 1
    override val outputJ: Int = (height - kernelSize + 2 * padding) / stride + 1
    override val outputK: Int = (width - kernelSize + 2 * padding) / stride + 1

    init {
        val numH = height - kernelSize + 2 * padding
        val numW = width - kernelSize + 2 * padding
        check(numH % stride == 0 && numW % stride == 0) {
            """
            invalid parameter.
            height: $height
            width: $width
            kernel: $kernel
            padding: $padding
            stride: $stride
            dilation: $dilation
            outputJ: ${(height - kernelSize + 2 * padding) / stride.toFloat() + 1}
            outputK: ${(width - kernelSize + 2 * padding) / stride.toFloat() + 1}
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val col = input.unfold(window = kernel, stride = stride, dilation = dilation, padding = padding)
            .reshapeToD3(i = channel, j = outputJ * outputK, k = kernel * kernel)
            .toD4()
            .transpose(axisI = 1, axisJ = 3, axisK = 0, axisL = 2)
            .reshapeToD2(i = channel * kernel * kernel, j = input.size * outputJ * outputK)
        return (weight.reshapeToD2(outputI, channel * kernel * kernel).matMul(col))
            .reshapeToD4(i = filter, j = input.size, k = outputJ, l = outputK)
            .transpose(axisI = 1, axisJ = 0, axisK = 2, axisL = 3)
            .toBatch()
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val col = input.unfold(window = kernel, stride = stride, dilation = dilation, padding = padding)
            .reshapeToD3(i = channel, j = outputJ * outputK, k = kernel * kernel)
            .toD4()
            .transpose(axisI = 1, axisJ = 3, axisK = 0, axisL = 2)
            .reshapeToD2(i = channel * kernel * kernel, j = input.size * outputJ * outputK)
        val output = (weight.reshapeToD2(i = outputI, j = channel * kernel * kernel).matMul(col))
            .reshapeToD4(i = filter, j = input.size, k = outputJ, l = outputK)
            .transpose(axisI = 1, axisJ = 0, axisK = 2, axisL = 3)
            .toBatch()

        val delta = calcDelta(output)

        val reversed = weight
            .reshapeToD2(i = filter, j = channel * kernel * kernel)
            .transpose()
        val deltaCol = delta.toD4()
            .transpose(axisI = 1, axisJ = 0, axisK = 2, axisL = 3)
            .reshapeToD2(i = filter, j = input.size * outputJ * outputK)
        val dx = (reversed.matMul(deltaCol))
            .reshapeToD4(i = channel, j = kernel * kernel, k = input.size, l = outputJ * outputK)
            .transpose(axisI = 2, axisJ = 0, axisK = 3, axisL = 1)
            .toBatch()
            .reshapeToD4(i = channel, j = outputJ, k = outputK, l = kernel * kernel)
            .fold(stride = stride, dilation = dilation, padding = padding)

        val dw = deltaCol.matMul(col.transpose())
            .reshapeToD4(i = filter, j = channel, k = kernel, l = kernel)
        weight = optimizer.adapt(weight = weight, dw = dw / input.size.toFloat()).toGlobal()

        return dx
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d4(i = filter, j = inputI, k = kernel, l = kernel)
    }
}

fun GraphBuilder.Node.D3.convD2(
    filter: Int,
    kernel: Int,
    stride: Int = 1,
    dilation: Int = 1,
    padding: Int = 0,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
    id: String = Uuid.random().toString(),
) = addCompute(
    compute =
        ConvD2(
            filter = filter,
            channel = inputI,
            kernel = kernel,
            stride = stride,
            dilation = dilation,
            padding = padding,
            height = inputJ,
            width = inputK,
            optimizer = optimizer.d4(filter, inputI, kernel, kernel),
            weight = initializer.d4(
                input = listOf(inputI, kernel, kernel),
                output = listOf(filter, kernel, kernel),
                i = filter,
                j = inputI,
                k = kernel,
                l = kernel,
            ),
            id = id,
        ),
)
