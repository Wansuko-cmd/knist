package com.wsr.layer.bias

import com.wsr.Network2
import com.wsr.common.IOType1d
import com.wsr.layer.Layer
import kotlin.random.Random

class Bias1d(
    override val numOfInput: Int,
    override val numOfOutput: Int,
    private val rate: Double,
    private val random: Random,
) : Layer<IOType1d> {
    private val weight = Array(numOfInput) { random.nextDouble(-1.0, 1.0) }
    override fun expect(input: IOType1d): IOType1d {
        return Array(numOfOutput) { input[it] + weight[it] }
    }

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        val output = Array(numOfOutput) { input[it] + weight[it] }
        val delta = delta(output)
        for (i in 0 until numOfOutput) {
            weight[i] -= rate * delta[i]
        }
        return delta
    }
}

fun Network2.Builder.bias1d() =
    addLayer(Bias1d(numOfInput = numOfInput, numOfOutput = numOfInput, rate = rate, random = random))

