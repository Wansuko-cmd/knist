package com.wsr.process.conv

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.average.average
import com.wsr.conv.convD1
import com.wsr.conv.deConvD1
import com.wsr.operator.minus
import com.wsr.operator.times
import com.wsr.process.Process
import com.wsr.reshape.toD2
import com.wsr.reshape.toD3
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable

@Serializable
class ConvD1 internal constructor(
    private val filter: Int,
    private val channel: Int,
    private val kernel: Int,
    private val stride: Int,
    private val padding: Int,
    private val inputSize: Int,
    private val rate: Double,
    private var weight: IOType.D3,
) : Process.D2() {
    override val outputX: Int = filter
    override val outputY: Int = (inputSize - kernel + 2 * padding) / stride + 1

    init {
        check((inputSize - kernel + 2 * padding) % stride == 0) {
            val output = (inputSize - kernel + 2 * padding) / stride.toDouble() + 1.0
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

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.convD1(weight, stride, padding)

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val output = input.convD1(weight, stride, padding)
        val delta = calcDelta(output)

        val reversed =
            IOType
                .d3(weight.shape) { x, y, z -> weight[x, y, kernel - z - 1] }
                .transpose(1, 0, 2)
        val dx = delta.deConvD1(reversed, stride, padding)

        val dw =
            List(input.size) { index ->
                (0 until filter)
                    .map { f ->
                        (0 until channel)
                            .map { c ->
                                input[index][c].deConvD1(delta[index][f], stride, padding)
                            }.toD2()
                    }.toD3()
            }
        weight -= rate * dw.average()

        return dx
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.convD1(filter: Int, kernel: Int, stride: Int = 1, padding: Int = 0) = addProcess(
    process =
    ConvD1(
        filter = filter,
        channel = inputX,
        kernel = kernel,
        stride = stride,
        padding = padding,
        inputSize = inputY,
        rate = rate,
        weight = IOType.d3(filter, inputX, kernel) { _, _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
