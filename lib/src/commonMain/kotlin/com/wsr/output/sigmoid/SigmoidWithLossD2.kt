package com.wsr.output.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.converter.Converter
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.output.Output
import com.wsr.output.TResult
import com.wsr.power.ln
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
internal class SigmoidWithLossD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun train(input: List<IOType.D2>, label: List<IOType.D2>): TResult<IOType.D2> {
        val output = input.map { input ->
            IOType.d2(outputX, outputY) { x, y -> 1 / (1 + exp(-input[x, y])) }
        }
        val one = List(label.size) { IOType.d2(outputX, outputY) { _, _ -> 1f } }
        val loss = run {
            val y = label * output.ln(1e-7f)
            val p = (one - label) * (one - output).ln(1e-7f)
            -(y + p).sum().average().toFloat()
        }
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
