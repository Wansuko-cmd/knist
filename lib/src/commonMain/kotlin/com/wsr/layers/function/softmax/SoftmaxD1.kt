package com.wsr.layers.function.softmax

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SoftmaxD1 internal constructor(override val outputSize: Int) : Layer.D1() {
    override fun expect(input: IOType.D1): IOType.D1 = input

    override fun train(
        input: IOType.D1,
        calcDelta: (IOType.D1) -> IOType.D1,
    ): IOType.D1 {
        val max = input.value.max()
        val exp = input.value.map { exp(it - max) }
        val sum = exp.sum()
        val output = IOType.d1(outputSize) { exp[it] / sum }
        return calcDelta(output)
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.softmax() = addLayer(SoftmaxD1(outputSize = inputSize))
