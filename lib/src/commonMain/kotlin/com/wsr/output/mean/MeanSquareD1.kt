package com.wsr.output.mean

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.output.Output
import com.wsr.operator.minus
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun loss(
        input: List<IOType.D1>,
        label: List<IOType.D1>,
    ): Float {
        TODO("Not yet implemented")
    }

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> = List(input.size) { i ->
        input[i] -
            label[i]
    }
}

fun <T> NetworkBuilder.D1<T>.meanSquare() = addOutput(MeanSquareD1(inputSize))

fun <I, O> NetworkBuilder.D1<I>.meanSquare(converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>) = addOutput(
    output = MeanSquareD1(inputSize),
    converter = converter,
)
