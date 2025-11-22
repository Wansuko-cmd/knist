package com.wsr.output.mean

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.average.average
import com.wsr.batch.average.batchAverage
import com.wsr.batch.func.pow
import com.wsr.batch.minus.minus
import com.wsr.collection.average
import com.wsr.converter.Converter
import com.wsr.operator.minus
import com.wsr.output.Output
import com.wsr.output.TResult
import com.wsr.power.pow
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: Batch<IOType.D1>): Batch<IOType.D1> = input

    override fun train(input: Batch<IOType.D1>, label: Batch<IOType.D1>): TResult<IOType.D1> {
        val delta = input - label
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
