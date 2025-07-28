package com.wsr

import com.wsr.common.IOType1d
import com.wsr.common.IOType2d

class Affine1d(
    private val numOfInput: Int,
    private val numOfNeuron: Int,
    private val rate: Double,
) : Layer<IOType1d> {
    private val weight: IOType2d =
        Array(numOfInput) { Array(numOfNeuron) { random.nextDouble(-1.0, 1.0) } }
    override fun expect(input: IOType1d): IOType1d = forward(input)

    override fun train(input: IOType1d, delta: (IOType1d) -> IOType1d): IOType1d {
        val output = forward(input)
        val delta = delta(output)
        val dx = Array(numOfInput) { inputIndex ->
            var sum = 0.0
            for (outputIndex in 0 until numOfNeuron) {
                sum += delta[outputIndex] * weight[inputIndex][outputIndex]
            }
            sum
        }
        for (inputIndex in 0 until numOfInput) {
            for (outputIndex in 0 until numOfNeuron) {
                weight[inputIndex][outputIndex] -= rate * delta[outputIndex] * input[inputIndex]
            }
        }
        return dx
    }

    private fun forward(input: IOType1d): IOType1d {
        return Array(numOfNeuron) { outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until numOfInput) {
                sum += input[inputIndex] * weight[inputIndex][outputIndex]
            }
            sum
        }
    }
}
