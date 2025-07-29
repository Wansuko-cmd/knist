package com.wsr.layers.affine

import com.wsr.Network
import com.wsr.common.IOTypeD1
import com.wsr.common.IOTypeD2
import com.wsr.layers.Layer
import kotlin.random.Random

class AffineD1 internal constructor(
    override val numOfInput: Int,
    override val numOfOutput: Int,
    private val rate: Double,
    private val random: Random,
) : Layer {
    private val weight: IOTypeD2 =
        Array(numOfInput) { Array(numOfOutput) { random.nextDouble(-1.0, 1.0) } }

    override fun expect(input: IOTypeD1): IOTypeD1 = forward(input)

    override fun train(input: IOTypeD1, delta: (IOTypeD1) -> IOTypeD1): IOTypeD1 {
        val output = forward(input)
        val delta = delta(output)
        val dx = Array(numOfInput) { inputIndex ->
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

    private fun forward(input: IOTypeD1): IOTypeD1 {
        return Array(numOfOutput) { outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until numOfInput) {
                sum += input[inputIndex] * weight[inputIndex][outputIndex]
            }
            sum
        }
    }
}

fun Network.Builder.affineD1(numOfOutput: Int) =
    addLayer(
        layer = AffineD1(
            numOfInput = numOfInput,
            numOfOutput = numOfOutput,
            rate = rate,
            random = random,
        ),
    )
