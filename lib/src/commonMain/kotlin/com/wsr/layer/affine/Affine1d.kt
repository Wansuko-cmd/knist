package com.wsr.layer.affine

import com.wsr.Network2
import com.wsr.common.IOType1d
import com.wsr.common.IOType2d
import com.wsr.layer.Layer
import kotlin.random.Random

class Affine1d internal constructor(
    override val numOfInput: Int,
    override val numOfOutput: Int,
    private val rate: Double,
    private val random: Random,
) : Layer<IOType1d> {
    private val weight: IOType2d =
        Array(numOfInput) { Array(numOfOutput) { random.nextDouble(-1.0, 1.0) } }
    override fun expect(input: IOType1d): IOType1d = forward(input)

    override fun train(input: IOType1d, delta: (IOType1d) -> IOType1d): IOType1d {
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

    private fun forward(input: IOType1d): IOType1d {
        return Array(numOfOutput) { outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until numOfInput) {
                sum += input[inputIndex] * weight[inputIndex][outputIndex]
            }
            sum
        }
    }
}

fun Network2.Builder.affine1d(numOfOutput: Int) =
    addLayer(Affine1d(numOfInput = numOfInput, numOfOutput = numOfOutput, rate = rate, random = random))

