package com.wsr.layers.affine

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    val inputSize: Int,
    override val outputSize: Int,
    private val rate: Double,
    private val weight: IOType.D2,
) : Layer.D1() {
    override fun expect(input: IOType.D1): IOType.D1 = forward(input)

    override fun train(input: IOType.D1, calcDelta: (IOType.D1) -> IOType.D1): IOType.D1 {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = IOType.d1(inputSize) { inputIndex ->
            var sum = 0.0
            for (outputIndex in 0 until outputSize) {
                sum += delta[outputIndex] * weight[inputIndex, outputIndex]
            }
            sum
        }
        for (inputIndex in 0 until inputSize) {
            for (outputIndex in 0 until outputSize) {
                weight[inputIndex, outputIndex] -= rate * delta[outputIndex] * input[inputIndex]
            }
        }
        return dx
    }

    private fun forward(input: IOType.D1): IOType.D1 {
        return IOType.d1(outputSize) { outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until inputSize) {
                sum += input[inputIndex] * weight[inputIndex, outputIndex]
            }
            sum
        }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.affine(neuron: Int) =
    addLayer(
        layer = AffineD1(
            inputSize = inputSize,
            outputSize = neuron,
            rate = rate,
            weight = IOType.d2(inputSize, neuron) { _, _ -> random.nextDouble(-1.0, 1.0) },
        ),
    )