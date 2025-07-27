package com.wsr

import com.wsr.common.IOType1d

class Bias1d(numOfInput: Int) : Layer<IOType1d> {
    private val weight = Array(numOfInput) { 0.0 }
    override fun expect(input: IOType1d): IOType1d {
        for (i in input.indices) {
            input[i] += weight[i]
        }
        return input
    }

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        for (i in input.indices) {
            input[i] += weight[i]
        }
        val delta = delta(input)
        for (i in weight.indices) {
            weight[i] -= delta[i]
        }
        return delta
    }
}
