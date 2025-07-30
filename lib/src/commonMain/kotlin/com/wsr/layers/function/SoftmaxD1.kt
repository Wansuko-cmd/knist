package com.wsr.layers.function

import com.wsr.Network
import com.wsr.common.IOTypeD1
import com.wsr.layers.Layer
import kotlin.math.exp

class SoftmaxD1 internal constructor(override val numOfInput: Int) : Layer() {
    override val numOfOutput = numOfInput
    override fun expect(input: IOTypeD1): IOTypeD1 = input

    override fun train(
        input: IOTypeD1,
        delta: (IOTypeD1) -> IOTypeD1,
    ): IOTypeD1 {
        val max = input.max()
        val exp = input.map { exp(it - max) }
        val sum = exp.sum()
        val output = Array(numOfOutput) { exp[it] / sum }
        return delta(output)
    }
}

fun Network.Builder.softmaxD1() =
    addLayer(SoftmaxD1(numOfInput = numOfInput))
