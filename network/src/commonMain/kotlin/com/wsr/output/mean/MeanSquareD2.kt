package com.wsr.output.mean

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.batchAverage
import com.wsr.batch.math.pow
import com.wsr.batch.operation.minus.minus
import com.wsr.converter.Converter
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.output.Output
import com.wsr.output.TResult
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: Batch<IOType.D2>): Batch<IOType.D2> = input

    override fun train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val delta = input - label(input)
        val loss = delta
            .pow(2)
            .batchAverage().average() * 0.5f
        return TResult(loss = loss, delta = delta)
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
