package com.wsr.layer.output.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.layer.output.Output
import com.wsr.operator.minus
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SigmoidWithLossD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun train(input: List<IOType.D2>, label: List<IOType.D2>): List<IOType.D2> {
        val output = input.map { input ->
            IOType.d2(outputX, outputY) { x, y -> 1 / (1 + exp(-input[x, y])) }
        }
        return output - label
    }
}

fun <T> NetworkBuilder.D2<T>.sigmoidWithLoss() = addOutput(
    output = SigmoidWithLossD2(
        outputX = inputX,
        outputY = inputY,
    ),
)

fun <I, O> NetworkBuilder.D2<I>.sigmoidWithLoss(converter: NetworkBuilder.D2<I>.() -> Converter.D2<O>) = addOutput(
    output = SigmoidWithLossD2(
        outputX = inputX,
        outputY = inputY,
    ),
    converter = converter,
)
