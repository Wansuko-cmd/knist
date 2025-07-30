package com.wsr.layers.bias

import com.wsr.Network
import com.wsr.common.IOTypeD1
import com.wsr.layers.Layer
import kotlin.random.Random

class BiasD1 internal constructor(
    override val numOfInput: Int,
    private val rate: Double,
    private val random: Random,
) : Layer() {
    override val numOfOutput = numOfInput
    private val weight = Array(numOfInput) { random.nextDouble(-1.0, 1.0) }
    override fun expect(input: IOTypeD1): IOTypeD1 {
        return Array(numOfOutput) { input[it] + weight[it] }
    }

    override fun train(
        input: IOTypeD1,
        delta: (IOTypeD1) -> IOTypeD1,
    ): IOTypeD1 {
        val output = Array(numOfOutput) { input[it] + weight[it] }
        val delta = delta(output)
        for (i in 0 until numOfOutput) {
            weight[i] -= rate * delta[i]
        }
        return delta
    }
}

fun Network.Builder.biasD1() =
    addLayer(
        BiasD1(
            numOfInput = numOfInput,
            rate = rate,
            random = random,
        ),
    )

