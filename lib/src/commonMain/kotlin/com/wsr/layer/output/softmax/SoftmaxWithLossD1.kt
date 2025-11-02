package com.wsr.layer.output.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.layer.output.Output
import com.wsr.operator.div
import com.wsr.operator.minus
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
internal class SoftmaxWithLossD1 internal constructor(
    val outputSize: Int,
    val temperature: Double,
) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.also { println(it) }

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> {
        val input = input / temperature
        val output = input.map { (value) ->
                val max = value.max()
                val exp = value.map { exp(it - max) }
                val sum = exp.sum()
                IOType.d1(outputSize) { exp[it] / sum }
            }
        return List(input.size) { i -> output[i] - label[i] }
    }
}

fun <T> NetworkBuilder.D1<T>.softmaxWithLoss(
    temperature: Double = 1.0,
) = addOutput(
    output = SoftmaxWithLossD1(
        outputSize = inputSize,
        temperature = temperature,
    ),
)

fun <I, O> NetworkBuilder.D1<I>.softmaxWithLoss(
    converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>,
    temperature: Double = 1.0,
) = addOutput(
    output = SoftmaxWithLossD1(
        outputSize = inputSize,
        temperature = temperature,
    ),
    converter = converter,
)
