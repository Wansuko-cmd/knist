package com.wsr.layers.function

import com.wsr.Network
import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class ReluD1 internal constructor(override val numOfInput: Int) : Layer.D1() {
    override val numOfOutput = numOfInput
    override fun expect(input: IOType.D1): IOType.D1 = forward(input)

    override fun train(
        input: IOType.D1,
        delta: (IOType.D1) -> IOType.D1,
    ): IOType.D1 {
        val output = forward(input)
        val delta = delta(output)
        return IOType.D1(numOfOutput) { if (output[it] <= 0.0) 0.0 else delta[it] }
    }

    private fun forward(input: IOType.D1): IOType.D1 {
        return IOType.D1(numOfOutput) { input[it].coerceAtLeast(0.0) }
    }
}

fun Network.Builder.reluD1() = addLayer(ReluD1(numOfInput = numOfInput))

fun <T : IOType> NetworkBuilder.D1<T>.relu() = addLayer(ReluD1(numOfInput = numOfInput))
