package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphScope.addOutput
import com.wsr.knist.network.converter.Converter
import com.wsr.knist.network.converter.raw.RawD3
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.output.TResult
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD3 internal constructor() : Output.D3() {
    override fun IOScope.expect(input: Batch<IOType.D3>): Batch<IOType.D3> = input

    override fun IOScope.train(input: Batch<IOType.D3>, label: (Batch<IOType.D3>) -> Batch<IOType.D3>): TResult<IOType.D3> {
        val delta = input - label(input)
        val loss = delta
            .pow(2)
            .batchAverage().average() * 0.5f
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D3.meanSquare() = addOutput(
    output = MeanSquareD3(),
    converter = RawD3(inputI, inputJ, inputK),
)

fun <O> GraphBuilder.Node.D3.meanSquare(converter: GraphBuilder.Node.D3.() -> Converter.D3<O>) = addOutput(
    output = MeanSquareD3(),
    converter = converter(),
)
