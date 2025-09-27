package com.wsr.layers.conv

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.d1.average
import com.wsr.d1.plus
import com.wsr.d1.times
import com.wsr.d1.toD2
import com.wsr.d2.convD1
import com.wsr.d2.deConvD1
import com.wsr.d2.plus
import com.wsr.d2.times
import com.wsr.d2.toD3
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
    private var weight: IOType.D3,
) : Layer.D2() {
    override val outputX: Int = filter
    override val outputY: Int = (inputSize - kernel + 2 * padding) / stride + 1

    init {
        check((inputSize - kernel + 2 * padding) % stride == 0)
    }

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        val dw = List(input.size) { index -> input[index].deConvD1(delta[index]) }
            .map { v ->  channel.toDouble() * v }
            .let { rate * it.average() }

        for (c in 0 until channel) {
            for (f in 0 until filter) {
                for (k in 0 until kernel) {
                    weight[c, f, k] -= dw[k]
                }
            }
        }

        // TODO dxを計算する
        return input
    }

    private fun forward(input: IOType.D2): IOType.D2 = (0 until filter)
        .map { input.convD1(weight[it], stride, padding) }
        .toD2()
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
        weight = IOType.d3(filter, inputX, kernel) { _, _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
