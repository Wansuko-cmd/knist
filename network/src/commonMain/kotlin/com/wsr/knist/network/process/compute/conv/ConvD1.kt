package com.wsr.knist.network.process.compute.conv

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD3
import com.wsr.knist.batch.shape.toD4
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.shape.reshapeToD2
import com.wsr.knist.core.shape.reshapeToD3
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
class ConvD1 internal constructor(
    private val filter: Int,
    private val channel: Int,
    private val kernel: Int,
    private val stride: Int,
    private val dilation: Int,
    private val padding: Int,
    private val inputSize: Int,
    private var optimizer: Optimizer.D3,
    private var weight: IOType.D3.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val inputI: Int = channel
    override val inputJ: Int = inputSize

    override val outputI: Int = filter
    private val kernelSize = (kernel - 1) * dilation + 1
    override val outputJ: Int = (inputSize - kernelSize + 2 * padding) / stride + 1

    init {
        check((inputSize - kernelSize + 2 * padding) % stride == 0) {
            val output = (inputSize - kernelSize + 2 * padding) / stride.toFloat() + 1.0
            """
            invalid parameter.
            inputSize: $inputSize
            kernel: $kernel
            padding: $padding
            stride: $stride
            dilation: $dilation
            output: $output
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val col = input.unfold(window = kernel, stride = stride, dilation = dilation, padding = padding)
            .toD4()
            .transpose(axisI = 1, axisJ = 3, axisK = 0, axisL = 2)
            .reshapeToD2(i = kernel * channel, j = outputJ * input.size)
        return (weight.reshapeToD2(outputI, channel * kernel).matMul(col))
            .reshapeToD3(i = filter, j = input.size, k = outputJ)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .toBatch()
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val col = input.unfold(window = kernel, stride = stride, dilation = dilation, padding = padding)
            .toD4()
            .transpose(axisI = 1, axisJ = 3, axisK = 0, axisL = 2)
            .reshapeToD2(i = kernel * channel, j = outputJ * input.size)
        val output = (weight.reshapeToD2(i = outputI, j = channel * kernel).matMul(col))
            .reshapeToD3(i = filter, j = input.size, k = outputJ)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .toBatch()

        val delta = calcDelta(output)

        val reversed = weight
            .reshapeToD2(i = filter, j = channel * kernel)
            .transpose()
        val deltaCol = delta.toD3()
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = filter, j = input.size * outputJ)
        val dx = (reversed.matMul(deltaCol))
            .reshapeToD4(i = channel, j = kernel, k = input.size, l = outputJ)
            .transpose(axisI = 2, axisJ = 0, axisK = 3, axisL = 1)
            .toBatch()
            .fold(stride = stride, dilation = dilation, padding = padding)

        val dw = deltaCol.matMul(col.transpose())
            .reshapeToD3(i = filter, j = channel, k = kernel)
        weight = optimizer.adapt(weight = weight, dw = dw / input.size.toFloat()).toGlobal()

        return dx
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d3(i = filter, j = inputI, k = kernel)
    }
}

fun GraphBuilder.Node.D2.convD1(
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
        ConvD1(
            filter = filter,
            channel = inputI,
            kernel = kernel,
            stride = stride,
            dilation = dilation,
            padding = padding,
            inputSize = inputJ,
            optimizer = optimizer.d3(filter, inputI, kernel),
            weight = initializer.d3(
                input = listOf(inputI, kernel),
                output = listOf(filter, kernel),
                i = filter,
                j = inputI,
                k = kernel,
            ),
            id = id,
        ),
)
