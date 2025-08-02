package com.wsr.layers.function

import com.wsr.Network
import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SigmoidD1 internal constructor(override val numOfInput: Int) : Layer.D1() {
    override val numOfOutput = numOfInput
    override fun expect(input: IOType.D1): IOType.D1 = input

    override fun train(
        input: IOType.D1,
        delta: (IOType.D1) -> IOType.D1,
    ): IOType.D1 {
        val output = IOType.D1(numOfInput) { 1 / (1 + exp(-input[it])) }
        val delta = delta(output)
        return IOType.D1(numOfOutput) { delta[it] * (1 - delta[it]) }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.sigmoid() = addLayer(SigmoidD1(numOfInput = numOfInput))
