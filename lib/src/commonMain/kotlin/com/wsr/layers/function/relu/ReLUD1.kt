package com.wsr.layers.function.relu

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class ReLUD1 internal constructor(override val outputSize: Int) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.map(::forward)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i -> IOType.d1(outputSize) { if (output[i][it] <= 0.0) 0.0 else delta[i][it] } }
    }

    private fun forward(input: IOType.D1): IOType.D1 {
        return IOType.d1(outputSize) { input[it].coerceAtLeast(0.0) }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.reLU() = addLayer(ReLUD1(outputSize = inputSize))
