package com.wsr.process.conv

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.average.average
import com.wsr.d1.convD1
import com.wsr.d1.deConvD1
import com.wsr.d1.sum
import com.wsr.d1.toD2
import com.wsr.d2.toD3
import com.wsr.d3.minus
import com.wsr.d3.times
import com.wsr.d3.transpose
import com.wsr.process.Process
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

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)

        val reversed = IOType.d3(weight.shape) { x, y, z ->
            val z = weight.shape[2] - z - 1
            weight[x, y, z]
        }
            .transpose(1, 0, 2)
        val dx = List(input.size) { index ->
            (0 until channel).map { c ->
                (0 until filter)
                    .map { f -> delta[index][f].deConvD1(reversed[c][f], stride, padding) }
                    .sum()
            }
                .toD2()
        }

        val dw = List(input.size) { index ->
            (0 until filter).map { f ->
                (0 until channel).map { c ->
                    input[index][c].deConvD1(delta[index][f], stride, padding)
                }.toD2()
            }.toD3()
        }
        weight -= rate * dw.average()

        return dx
    }

    private fun forward(input: IOType.D2): IOType.D2 = (0 until filter)
        .map { f ->
            (0 until channel)
                .map { c -> input[c].convD1(weight[f, c], stride, padding) }
                .sum()
        }
        .toD2()
}

fun <T : IOType> NetworkBuilder.D2<T>.convD1(
    filter: Int,
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
) = addProcess(
    process = ConvD1(
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
