package com.wsr.layer.function

import com.wsr.Network2
import com.wsr.layer.Layer
import com.wsr.common.IOType1d
import kotlin.math.exp

class Sigmoid1d(
    override val numOfInput: Int,
    override val numOfOutput: Int,
) : Layer<IOType1d> {
    override fun expect(input: IOType1d): IOType1d = input

    override fun train(
        input: IOType1d,
        delta: (IOType1d) -> IOType1d,
    ): IOType1d {
        val output = Array(numOfInput) { 1 / (1 + exp(-input[it])) }
        val delta = delta(output)
        return Array(numOfOutput) { delta[it] * (1 - delta[it]) }
    }
}

fun Network2.Builder.sigmoid1d() = addLayer(Sigmoid1d(numOfInput = numOfInput, numOfOutput = numOfInput))
