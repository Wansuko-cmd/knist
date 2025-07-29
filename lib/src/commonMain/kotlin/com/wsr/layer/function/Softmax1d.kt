package com.wsr.layer.function

import com.wsr.Network2
import com.wsr.common.IOType1d
import com.wsr.layer.Layer
import kotlin.math.exp

class Softmax1d(
    override val numOfInput: Int,
    override val numOfOutput: Int,
) : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = input

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        val max = input.max()
        val exp = input.map { exp(it - max) }
        val sum = exp.sum()
        val output = Array(numOfOutput) { exp[it] / sum }
        return delta(output)
    }
}

fun Network2.Builder.softmax1d() = addLayer(Softmax1d(numOfInput = numOfInput, numOfOutput = numOfInput))
