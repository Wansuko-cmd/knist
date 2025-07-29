package com.wsr.layer.function

import com.wsr.Network2
import com.wsr.common.IOType1d
import com.wsr.layer.Layer

class Relu1d(
    override val numOfInput: Int,
    override val numOfOutput: Int,
) : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = forward(input)

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d
    ): IOType1d {
        val output = forward(input)
        val delta = delta(output)
        return Array(numOfOutput) { if (output[it] <= 0.0) 0.0 else delta[it] }
    }

    private fun forward(input: IOType1d): IOType1d {
        return Array(numOfOutput) { input[it].coerceAtLeast(0.0) }
    }
}

fun Network2.Builder.relu1d() = addLayer(Relu1d(numOfInput = numOfInput, numOfOutput = numOfInput))
