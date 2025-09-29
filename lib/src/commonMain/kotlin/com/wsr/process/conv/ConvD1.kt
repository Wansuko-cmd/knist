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
import com.wsr.sum.sum
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

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = forward(input)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = forward(input)
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

//    private fun forward(input: List<IOType.D2>): List<IOType.D2> = input.map { input ->
//        (0 until filter)
//            .map { f ->
//                (0 until channel)
//                    .map { c -> input[c].convD1(weight[f, c], stride, padding) }
//                    .sum()
//            }
//            .toD2()
//    }

    private fun forward(input: List<IOType.D2>): List<IOType.D2> {
        val col = input.im2col(kernel, stride, padding)
        val filter = weight.toCol()
        val result = Array(filter.size) { DoubleArray(col.size) }
        for (f in filter.indices) {
            for (i in col.indices) {
                result[f][i] = col[i] dot filter[f]
            }
        }
        return List(input.size) { b ->
            IOType.d2(outputX, outputY) { f, o ->
                result[f][b * outputY + o]
            }
        }
    }


    private fun List<IOType.D2>.im2col(
        kernel: Int,
        stride: Int = 1,
        padding: Int = 0,
    ): Array<DoubleArray> {
        val (channel, inputSize) = first().shape
        val output = (inputSize - kernel + 2 * padding) / stride + 1
        val result = Array(this.size * output) { DoubleArray(kernel * channel) }
        this.forEachIndexed { index, ioType ->
            for (o in 0 until output) {
                val row = index * output + o
                for (k in 0 until kernel) {
                    val inputIndex = o * stride + k - padding
                    if (inputIndex in 0 until inputSize) {
                        for (c in 0 until channel) {
                            result[row][c * kernel + k] = ioType[c, inputIndex]
                        }
                    }
                }
            }
        }
        return result
    }

    private fun IOType.D3.toCol(): Array<DoubleArray> {
        val filterCount = shape[0]
        val channels = shape[1]
        val kernel = shape[2]

        return Array(filterCount) { f ->
            DoubleArray(channels * kernel) { i ->
                val c = i / kernel
                val k = i % kernel
                this[f, c, k]
            }
        }
    }

    private infix fun DoubleArray.dot(other: DoubleArray): Double {
        var sum = 0.0
        for (i in indices) {
            sum += this[i] * other[i]
        }
        return sum
    }
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
