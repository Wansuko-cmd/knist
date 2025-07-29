package com.wsr.layers.function

import com.wsr.Network
import com.wsr.common.IOTypeD1
import com.wsr.layers.Layer

class ReluD1 internal constructor(
    override val numOfInput: Int,
    override val numOfOutput: Int,
) : Layer {
    override fun expect(input: IOTypeD1): IOTypeD1 = forward(input)

    override fun train(
        input: IOTypeD1,
        delta: (IOTypeD1) -> IOTypeD1,
    ): IOTypeD1 {
        val output = forward(input)
        val delta = delta(output)
        return Array(numOfOutput) { if (output[it] <= 0.0) 0.0 else delta[it] }
    }

    private fun forward(input: IOTypeD1): IOTypeD1 {
        return Array(numOfOutput) { input[it].coerceAtLeast(0.0) }
    }
}

fun Network.Builder.reluD1() = addLayer(ReluD1(numOfInput = numOfInput, numOfOutput = numOfInput))
