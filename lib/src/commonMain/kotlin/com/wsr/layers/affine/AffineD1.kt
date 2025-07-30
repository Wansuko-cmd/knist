package com.wsr.layers.affine

import com.wsr.Network
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    override val numOfInput: Int,
    override val numOfOutput: Int,
    private val rate: Double,
    private val weight: IOType.D2,
) : Layer.D1() {
    override fun expect(input: IOType.D1): IOType.D1 = forward(input)

    override fun train(input: IOType.D1, delta: (IOType.D1) -> IOType.D1): IOType.D1 {
        val output = forward(input)
        val delta = delta(output)
        val dx = IOType.D1(numOfInput) { inputIndex ->
            var sum = 0.0
            for (outputIndex in 0 until numOfOutput) {
                sum += delta[outputIndex] * weight[inputIndex][outputIndex]
            }
            sum
        }
        for (inputIndex in 0 until numOfInput) {
            for (outputIndex in 0 until numOfOutput) {
                weight[inputIndex][outputIndex] -= rate * delta[outputIndex] * input[inputIndex]
            }
        }
        return dx
    }

    private fun forward(input: IOType.D1): IOType.D1 {
        return IOType.D1(numOfOutput) { outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until numOfInput) {
                sum += input[inputIndex] * weight[inputIndex][outputIndex]
            }
            sum
        }
    }
}

fun Network.Builder.affineD1(neuron: Int) =
    addLayer(
        layer = AffineD1(
            numOfInput = numOfInput,
            numOfOutput = neuron,
            rate = rate,
            weight = IOType.D2(numOfInput) { IOType.D1(neuron) { random.nextDouble(-1.0, 1.0) } },
        ),
    )
