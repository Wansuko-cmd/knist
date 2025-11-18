package com.wsr.output.mean

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.output.Output
import com.wsr.operator.minus
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input

    override fun loss(input: List<IOType.D2>, label: List<IOType.D2>): Float {
        TODO("Not yet implemented")
    }

    override fun train(input: List<IOType.D2>, label: List<IOType.D2>): List<IOType.D2> = input - label
}

fun <T> NetworkBuilder.D2<T>.meanSquare() = addOutput(
    output = MeanSquareD2(
        outputX = inputX,
        outputY = inputY,
    ),
)

fun <I, O> NetworkBuilder.D2<I>.meanSquare(converter: NetworkBuilder.D2<I>.() -> Converter.D2<O>) = addOutput(
    output = MeanSquareD2(
        outputX = inputX,
        outputY = inputY,
    ),
    converter = converter,
)
