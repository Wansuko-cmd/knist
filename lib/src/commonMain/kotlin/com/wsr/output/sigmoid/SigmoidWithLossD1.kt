package com.wsr.output.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.operator.minus
import com.wsr.output.Output
import com.wsr.output.TResult
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SigmoidWithLossD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): TResult<IOType.D1> {
        val output = input.map { (value) ->
            IOType.d1(outputSize) { 1 / (1 + exp(-value[it])) }
        }
        val loss = TODO()
        val delta = output - label
        return TResult(loss = loss, delta = delta)
    }
}

fun <T> NetworkBuilder.D1<T>.sigmoidWithLoss() = addOutput(SigmoidWithLossD1(inputSize))

fun <I, O> NetworkBuilder.D1<I>.sigmoidWithLoss(converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>) = addOutput(
    output = SigmoidWithLossD1(inputSize),
    converter = converter,
)
