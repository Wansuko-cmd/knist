package com.wsr.layers.function.softmax

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SoftmaxD1 internal constructor(override val outputSize: Int) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = forward(input)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        return List(input.size) { i -> IOType.d1(outputSize) { delta[i][it] * output[i][it] * (1 - output[i][it]) } }
    }

    private fun forward(input: List<IOType.D1>) = input.map { (value) ->
        val max = value.max()
        val exp = value.map { exp(it - max) }
        val sum = exp.sum()
        IOType.d1(outputSize) { exp[it] / sum }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.softmax() = addLayer(SoftmaxD1(outputSize = inputSize))
