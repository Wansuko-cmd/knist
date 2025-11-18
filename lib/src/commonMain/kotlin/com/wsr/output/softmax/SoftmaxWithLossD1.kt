package com.wsr.output.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.converter.Converter
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.times
import com.wsr.output.Output
import com.wsr.output.TResult
import kotlin.math.exp
import kotlinx.serialization.Serializable
import kotlin.math.ln

@Serializable
internal class SoftmaxWithLossD1 internal constructor(
    val outputSize: Int,
    val temperature: Float,
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

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): TResult<IOType.D1> {
        val input = input / temperature
        val output = input.map { (value) ->
            val max = value.max()
            val exp = value.map { exp(it - max) }
            val sum = exp.sum()
            IOType.d1(outputSize) { exp[it] / sum }
        }
        val loss = (output * label).sum()
            .map { -ln(it + 1e-7f) }
            .average()
            .toFloat()
        val delta = (output - label) * label.generateMask()
        return TResult(loss = loss, delta = delta)
    }

    private fun List<IOType.D1>.generateMask() = map { label ->
        val value = label.value
        IOType.d1(
            value = FloatArray(value.size) { if (value[it] == maskValue?.toFloat()) 0f else 1f },
        )
    }
}

fun <T> NetworkBuilder.D1<T>.softmaxWithLoss(temperature: Float = 1f, maskValue: Int? = null) = addOutput(
    output = SoftmaxWithLossD1(
        outputSize = inputSize,
        temperature = temperature,
        maskValue = maskValue,
    ),
)

fun <I, O> NetworkBuilder.D1<I>.softmaxWithLoss(
    converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>,
    temperature: Float = 1f,
    maskValue: Int? = null,
) = addOutput(
    output = SoftmaxWithLossD1(
        outputSize = inputSize,
        temperature = temperature,
        maskValue = maskValue,
    ),
    converter = converter,
)
