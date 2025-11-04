package com.wsr.layer.output.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.layer.output.Output
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.reshape.toD2
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
internal class SoftmaxWithLossD2 internal constructor(
    val outputX: Int,
    val outputY: Int,
    val temperature: Double,
) : Output.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun train(input: List<IOType.D2>, label: List<IOType.D2>): List<IOType.D2> {
        val input = input / temperature
        val output = input.map { input ->
            (0 until outputX)
                .map { input[it] }
                .map { (value) ->
                    val max = value.max()
                    val exp = value.map { exp(it - max) }
                    val sum = exp.sum()
                    IOType.d1(outputY) { exp[it] / sum }
                }
                .toD2()
        }
        return List(input.size) { i -> output[i] - label[i] }
    }
}

fun <T> NetworkBuilder.D2<T>.softmaxWithLoss(temperature: Double = 1.0) = addOutput(
    output = SoftmaxWithLossD2(
        outputX = inputX,
        outputY = inputY,
        temperature = temperature,
    ),
)

fun <I, O> NetworkBuilder.D2<I>.softmaxWithLoss(
    converter: NetworkBuilder.D2<I>.() -> Converter.D2<O>,
    temperature: Double = 1.0,
) = addOutput(
    output = SoftmaxWithLossD2(
        outputX = inputX,
        outputY = inputY,
        temperature = temperature,
    ),
    converter = converter,
)
