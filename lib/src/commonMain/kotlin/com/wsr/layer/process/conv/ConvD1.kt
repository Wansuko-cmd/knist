package com.wsr.layer.process.conv

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.conv.convD1
import com.wsr.conv.deConvD1
import com.wsr.d3
import com.wsr.get
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.toD2
import com.wsr.reshape.toD3
import com.wsr.reshape.transpose
import com.wsr.set
import com.wsr.toBatch
import com.wsr.toList
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

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> =
        input.toList().convD1(weight, stride, padding).toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val input = input.toList()
        val output = input.convD1(weight, stride, padding)
        val delta = calcDelta(output.toBatch()).toList()

        val reversed =
            IOType
                .d3(weight.shape) { x, y, z -> weight[x, y, kernel - z - 1] }
                .transpose(1, 0, 2)
        val dx = delta.deConvD1(reversed, stride, padding)

        val dw = List(input.size) { index ->
            (0 until filter)
                .map { f ->
                    (0 until channel)
                        .map { c ->
                            input[index][c].convD1(delta[index][f], stride, padding)
                        }.toD2()
                }.toD3()
        }
        weight = optimizer.adapt(weight = weight, dw = dw.toBatch())

        return dx.toBatch()
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
