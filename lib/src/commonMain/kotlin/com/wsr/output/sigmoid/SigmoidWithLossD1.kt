package com.wsr.output.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.output.Output
import com.wsr.operator.minus
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SigmoidWithLossD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun loss(input: List<IOType.D1>, label: List<IOType.D1>): Float {
        TODO("Not yet implemented")
    }

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> {
        val output = input.map { (value) ->
            IOType.d1(outputSize) { 1 / (1 + exp(-value[it])) }
        }
        return output - label
    }
}

fun <T> NetworkBuilder.D1<T>.sigmoidWithLoss() = addOutput(SigmoidWithLossD1(inputSize))

fun <I, O> NetworkBuilder.D1<I>.sigmoidWithLoss(converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>) = addOutput(
    output = SigmoidWithLossD1(inputSize),
    converter = converter,
)
