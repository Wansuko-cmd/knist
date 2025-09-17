package com.wsr.layers.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class LeakyReLUD1 internal constructor(override val outputSize: Int) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.map(::forward)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d1(outputSize) { if (input[i][it] >= 0.0) delta[i][it] else 0.01 * delta[i][it] }
        }
    }

    private fun forward(input: IOType.D1): IOType.D1 {
        return IOType.d1(outputSize) { if (input[it] >= 0.0) input[it] else 0.01 }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.leakyReLU() = addLayer(LeakyReLUD1(outputSize = inputSize))
