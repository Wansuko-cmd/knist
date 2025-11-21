package com.wsr.output.sigmoid

import com.wsr.Batch
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
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SigmoidWithLossD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: Batch<IOType.D1>): Batch<IOType.D1> = input

    override fun train(input: Batch<IOType.D1>, label: Batch<IOType.D1>): TResult<IOType.D1> {
        val input = input.toList()
        val label = label.toList()
        val output = input.map { (value) ->
            IOType.d1(outputSize) { 1 / (1 + exp(-value[it])) }
        }
        val one = List(label.size) { IOType.d1(FloatArray(outputSize) { 1f }) }
        val loss = run {
            val y = label * output.ln(1e-7f)
            val p = (one - label) * (one - output).ln(1e-7f)
            -(y + p).sum().average().toFloat()
        }
        val delta = output - label
        return TResult(loss = loss, delta = delta.toBatch())
    }
}

fun <T> NetworkBuilder.D1<T>.sigmoidWithLoss() = addOutput(SigmoidWithLossD1(inputSize))

fun <I, O> NetworkBuilder.D1<I>.sigmoidWithLoss(converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>) = addOutput(
    output = SigmoidWithLossD1(inputSize),
    converter = converter,
)
