package com.wsr.layer.output.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.layer.output.Output
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.times
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SoftmaxWithLossD1 internal constructor(
    val outputSize: Int,
    val temperature: Double,
    val maskValue: Int? = null,
) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        val input = input / temperature
        return input.map { (value) ->
            val max = value.max()
            val exp = value.map { exp(it - max) }
            val sum = exp.sum()
            IOType.d1(outputSize) { exp[it] / sum }
        }
    }

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> {
        val input = input / temperature
        val output = input.map { (value) ->
            val max = value.max()
            val exp = value.map { exp(it - max) }
            val sum = exp.sum()
            IOType.d1(outputSize) { exp[it] / sum }
        }
        return (output - label) * label.generateMask()
    }

    private fun List<IOType.D1>.generateMask() = map { label ->
        val value = label.value
        IOType.d1(
            value = DoubleArray(value.size) { if (value[it] == maskValue?.toDouble()) 0.0 else 1.0 },
        )
    }
}

fun <T> NetworkBuilder.D1<T>.softmaxWithLoss(temperature: Double = 1.0, maskValue: Int? = null) = addOutput(
    output = SoftmaxWithLossD1(
        outputSize = inputSize,
        temperature = temperature,
        maskValue = maskValue,
    ),
)

fun <I, O> NetworkBuilder.D1<I>.softmaxWithLoss(
    converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>,
    temperature: Double = 1.0,
    maskValue: Int? = null,
) = addOutput(
    output = SoftmaxWithLossD1(
        outputSize = inputSize,
        temperature = temperature,
        maskValue = maskValue,
    ),
    converter = converter,
)
