package com.wsr

import com.wsr.common.IOType1d

class Bias1d(private val numOfInput: Int) : Layer<IOType1d> {
    private val weight = Array(numOfInput) { 0.0 }
    override fun expect(input: IOType1d): IOType1d {
        return Array(numOfInput) { input[it] + weight[it] }
    }

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        val output = Array(numOfInput) { input[it] + weight[it] }
        val delta = delta(output)
        for (i in weight.indices) {
            weight[i] -= delta[i]
        }
        return delta
    }
}
