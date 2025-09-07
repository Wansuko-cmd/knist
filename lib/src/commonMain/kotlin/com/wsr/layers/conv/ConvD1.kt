package com.wsr.layers.conv

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
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
    private val weight: IOType.D3,
) : Layer.D2() {
    override val outputX: Int = filter
    override val outputY: Int = (inputSize - kernel + 2 * padding) / stride + 1
    override fun expect(input: IOType.D2): IOType.D2 = forward(input.addPadding(padding))

    init {
        check((inputSize - kernel + 2 * padding) % stride == 0)
    }

    override fun train(
        input: IOType.D2,
        calcDelta: (IOType.D2) -> IOType.D2,
    ): IOType.D2 {
        val input = input.addPadding(padding)
        val output = forward(input)
        val delta = calcDelta(output)
        for (f in 0 until filter) {
            for (c in 0 until channel) {
                for (k in 0 until kernel) {
                    var sum = 0.0
                    for (d in 0 until outputY) {
                        sum += input[c, k + d * stride] * delta[f, d]
                    }
                    weight[f, c, k] -= (rate * sum)
                }
            }
        }
        // TODO dxを計算する
        return input
    }

    private fun forward(input: IOType.D2): IOType.D2 = IOType.D2(outputX, outputY) { filter, size ->
        var sum = 0.0
        for (c in 0 until channel) {
            for (k in 0 until kernel) {
                sum += input[c, size * stride + k] * weight[filter, c, k]
            }
        }
        sum
    }

    private fun IOType.D2.addPadding(padding: Int) = IOType.D2(
        x = shape[0],
        y = shape[1] + 2 * padding,
    ) { x, y ->
        if (y < padding || padding + shape[1] <= y) {
            0.0
        } else {
            this[x, y - padding]
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.convD1(
    filter: Int,
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
) = addLayer(
    layer = ConvD1(
        filter = filter,
        channel = inputX,
        kernel = kernel,
        stride = stride,
        padding = padding,
        inputSize = inputY,
        rate = rate,
        weight = IOType.D3(filter, inputX, kernel) { _, _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
