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
internal class MeanSquareD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: Batch<IOType.D1>): Batch<IOType.D1> = input

    override fun train(input: Batch<IOType.D1>, label: (Batch<IOType.D1>) -> Batch<IOType.D1>): TResult<IOType.D1> {
        val delta = input - label(input)
        val loss = delta
            .pow(2)
            .batchAverage().average() * 0.5f
        return TResult(loss = loss, delta = delta)
    }
}

fun <T> NetworkBuilder.D1<T>.meanSquare() = addOutput(MeanSquareD1(inputSize))

fun <I, O> NetworkBuilder.D1<I>.meanSquare(converter: NetworkBuilder.D1<I>.() -> Converter.D1<O>) = addOutput(
    output = MeanSquareD1(inputSize),
    converter = converter,
)
