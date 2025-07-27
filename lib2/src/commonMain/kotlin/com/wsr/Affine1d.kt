package com.wsr

import com.wsr.common.IOType1d
import com.wsr.common.IOType2d

class Affine1d(
    private val numOfInput: Int,
    private val numOfNeuron: Int,
) : Layer2<IOType1d> {
    private val weight: IOType2d = Array(numOfInput) { Array(numOfNeuron) { 0.0 } }
    override fun expect(input: IOType1d): IOType1d = forward(input)

    override fun train(input: IOType1d, delta: (IOType1d) -> IOType1d): IOType1d {
        val output = forward(input)
        val delta = delta(output)
        for (inputIndex in 0..numOfInput) {
            for (outputIndex in 0..numOfNeuron) {
                weight[inputIndex][outputIndex] = delta[outputIndex] * input[inputIndex]
            }
        }
        val before = Array(numOfInput) { 0.0 }
        for (inputIndex in 0..numOfInput) {
            var sum = 0.0
            for (outputIndex in 0..numOfNeuron) {
                sum += delta[outputIndex] * weight[inputIndex][outputIndex]
            }
            before[inputIndex] = sum
        }
        return before
    }

    private fun forward(input: IOType1d): IOType1d {
        val output = Array(numOfNeuron) { 0.0 }
        for (outputIndex in 0..numOfNeuron) {
            var sum = 0.0
            for (inputIndex in 0..numOfInput) {
                sum += input[inputIndex] * weight[inputIndex][outputIndex]
            }
            output[outputIndex] = sum
        }
        return output
    }
}
