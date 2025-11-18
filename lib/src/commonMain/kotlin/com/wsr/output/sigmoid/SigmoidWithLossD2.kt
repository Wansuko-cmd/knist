package com.wsr.output.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.output.Output
import com.wsr.operator.minus
import com.wsr.output.TResult
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SigmoidWithLossD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun train(input: List<IOType.D2>, label: List<IOType.D2>): TResult<IOType.D2> {
        val output = input.map { input ->
            IOType.d2(outputX, outputY) { x, y -> 1 / (1 + exp(-input[x, y])) }
        }
        val loss = TODO()
        val delta = output - label
        return TResult(loss = loss, delta = delta)
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
