package com.wsr.layers.function

import com.wsr.Network
import com.wsr.common.IOTypeD1
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SigmoidD1 internal constructor(override val numOfInput: Int) : Layer.D1() {
    override val numOfOutput = numOfInput
    override fun expect(input: IOTypeD1): IOTypeD1 = input

    override fun train(
        input: IOTypeD1,
        delta: (IOTypeD1) -> IOTypeD1,
    ): IOTypeD1 {
        val output = Array(numOfInput) { 1 / (1 + exp(-input[it])) }
        val delta = delta(output)
        return Array(numOfOutput) { delta[it] * (1 - delta[it]) }
    }
}

fun Network.Builder.sigmoidD1() =
    addLayer(SigmoidD1(numOfInput = numOfInput))
