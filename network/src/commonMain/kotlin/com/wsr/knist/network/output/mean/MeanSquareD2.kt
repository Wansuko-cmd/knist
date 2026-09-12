package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphScope.addOutput
import com.wsr.knist.network.converter.Converter
import com.wsr.knist.network.converter.raw.RawD2
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.output.TResult
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD2 internal constructor() : Output.D2() {
    override fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2> = input

    override fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val delta = input - label(input)
        val loss = delta
            .pow(2)
            .batchAverage().average() * 0.5f
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D2.meanSquare() = addOutput(
    output = MeanSquareD2(),
    converter = RawD2(inputI, inputJ),
)

fun <O> GraphBuilder.Node.D2.meanSquare(converter: GraphBuilder.Node.D2.() -> Converter.D2<O>) = addOutput(
    output = MeanSquareD2(),
    converter = converter(),
)
