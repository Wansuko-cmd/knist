package com.wsr

import com.wsr.common.IOType1d
import kotlin.random.Random

class Bias1d(private val numOfInput: Int, private val rate: Double) : Layer<IOType1d> {
    private val weight = Array(numOfInput) { Random.nextDouble(-1.0, 1.0) }
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
            weight[i] -= rate * delta[i]
        }
        return delta
    }
}
