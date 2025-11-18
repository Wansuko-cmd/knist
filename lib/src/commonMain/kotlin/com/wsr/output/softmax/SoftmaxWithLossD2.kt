package com.wsr.output.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.output.Output
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.times
import com.wsr.reshape.broadcastToD2
import com.wsr.reshape.toD2
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SoftmaxWithLossD2 internal constructor(
    val outputX: Int,
    val outputY: Int,
    val temperature: Float,
    val maskValue: Int? = null,
) : Output.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val input = input / temperature
        return input.map { input ->
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
    }

    override fun loss(input: List<IOType.D2>, label: List<IOType.D2>): Float {
        TODO("Not yet implemented")
    }

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

        return (output - label) * label.generateMask()
    }

    private fun List<IOType.D2>.generateMask(): List<IOType.D2> = when {
        maskValue == null -> List(size) {
            IOType.d2(shape = this[it].shape, value = FloatArray(this[it].value.size) { 1f })
        }

        else -> map { label ->
            IOType
                .d1(outputX) { seqId ->
                    val isPadding = label[seqId, maskValue] == 1f
                    if (isPadding) 0f else 1f
                }
                .broadcastToD2(0, outputY)
        }
    }
}

fun <T> NetworkBuilder.D2<T>.softmaxWithLoss(temperature: Float = 1f, maskValue: Int? = null) = addOutput(
    output = SoftmaxWithLossD2(
        outputX = inputX,
        outputY = inputY,
        temperature = temperature,
        maskValue = maskValue,
    ),
)

fun <I, O> NetworkBuilder.D2<I>.softmaxWithLoss(
    converter: NetworkBuilder.D2<I>.() -> Converter.D2<O>,
    temperature: Float = 1f,
    maskValue: Int? = null,
) = addOutput(
    output = SoftmaxWithLossD2(
        outputX = inputX,
        outputY = inputY,
        temperature = temperature,
        maskValue = maskValue,
    ),
    converter = converter,
)
