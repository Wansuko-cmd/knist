package com.wsr.output.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.operator.minus
import com.wsr.output.Output
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SoftmaxWithLossD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> {
        val output =
            input.map { (value) ->
                val max = value.max()
                val exp = value.map { exp(it - max) }
                val sum = exp.sum()
                IOType.d1(outputSize) { exp[it] / sum }
            }
        return List(input.size) { i -> output[i] - label[i] }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.softmaxWithLoss() = addOutput(SoftmaxWithLossD1(inputSize))
