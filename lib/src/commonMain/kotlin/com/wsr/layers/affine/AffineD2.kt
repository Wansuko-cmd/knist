package com.wsr.layers.affine

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class AffineD2 internal constructor(
    private val channel: Int,
    private val inputSize: Int,
    private val outputSize: Int,
    private val rate: Double,
    private val weight: IOType.D3,
) : Layer.D2() {
    override val outputX = channel
    override val outputY = outputSize

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        val dx = List(input.size) { i ->
            IOType.d2(channel, inputSize) { c, inputIndex ->
                var sum = 0.0
                for (outputIndex in 0 until outputSize) {
                    sum += delta[i][c, outputIndex] * weight[c, inputIndex, outputIndex]
                }
                sum
            }
        }
        for (c in 0 until channel) {
            for (inputIndex in 0 until inputSize) {
                for (outputIndex in 0 until outputSize) {
                    weight[c, inputIndex, outputIndex] -= rate * delta.sumOf { it[c, outputIndex] } * input.sumOf { it[c, inputIndex]}
                }
            }
        }
        return dx
    }

    private fun forward(input: IOType.D2): IOType.D2 {
        return IOType.d2(channel, outputSize) { c, outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until inputSize) {
                sum += input[c, inputIndex] * weight[c, inputIndex, outputIndex]
            }
            sum
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.affine(neuron: Int) =
    addLayer(
        layer = AffineD2(
            channel = inputX,
            inputSize = inputY,
            outputSize = neuron,
            rate = rate,
            weight = IOType.d3(inputX, inputY, neuron) { _, _, _ ->
                random.nextDouble(-1.0, 1.0)
            },
        ),
    )
