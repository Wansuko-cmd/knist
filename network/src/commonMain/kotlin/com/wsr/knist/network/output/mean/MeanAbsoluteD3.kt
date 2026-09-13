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
internal class MeanAbsoluteD3 internal constructor() : Output.D3() {
    override fun IOScope.expect(input: Batch<IOType.D3>): Batch<IOType.D3> = input

    override fun IOScope.train(input: Batch<IOType.D3>, label: (Batch<IOType.D3>) -> Batch<IOType.D3>): TResult<IOType.D3> {
        val diff = input - label(input)
        val condition = diff gt 0f
        val delta = where(condition = condition, onTrue = 1f, onFalse = -1f)
        val loss = where(condition = condition, onTrue = diff, onFalse = -1f * diff)
            .batchAverage().average()
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D3.meanAbsolute() = addOutput(
    output = MeanAbsoluteD3(),
    converter = RawD3(inputI, inputJ, inputK),
)

fun <O> GraphBuilder.Node.D3.meanAbsolute(converter: GraphBuilder.Node.D3.() -> Converter.D3<O>) = addOutput(
    output = MeanAbsoluteD3(),
    converter = converter(),
)
