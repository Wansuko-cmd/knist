package com.wsr.layers.function.sigmoid

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SigmoidD1 internal constructor(override val outputSize: Int) : Layer.D1() {
    override fun expect(input: IOType.D1): IOType.D1 = input

    override fun train(
        input: IOType.D1,
        calcDelta: (IOType.D1) -> IOType.D1,
    ): IOType.D1 {
        val output = IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) }
        val delta = calcDelta(output)
        return IOType.d1(outputSize) { delta[it] * output[it] * (1 - output[it]) }
    }

    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.map(::forward)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return delta.mapIndexed { i, d ->
            IOType.d1(outputSize) { d[it] * output[i][it] * (1 - output[i][it]) }
        }
    }

    private fun forward(input: IOType.D1) = IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) }
}

fun <T : IOType> NetworkBuilder.D1<T>.sigmoid() = addLayer(SigmoidD1(outputSize = inputSize))
