package com.wsr.output.mean

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.average
import com.wsr.converter.Converter
import com.wsr.operator.minus
import com.wsr.output.Output
import com.wsr.output.TResult
import com.wsr.power.pow
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: Batch<IOType.D2>): Batch<IOType.D2> = input

    override fun train(input: Batch<IOType.D2>, label: Batch<IOType.D2>): TResult<IOType.D2> {
        val input = input.toList()
        val label = label.toList()
        val delta = List(input.size) { i -> input[i] - label[i] }
        val loss = delta
            .pow(2)
            .average().average()
            .toFloat() * 0.5f
        return TResult(loss = loss, delta = delta.toBatch())
    }
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
