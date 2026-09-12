package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphScope.addOutput
import com.wsr.knist.network.converter.Converter
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.output.TResult
import kotlinx.serialization.Serializable

@Serializable
internal class MeanAbsoluteD1 internal constructor() : Output.D1() {
    override fun IOScope.expect(input: Batch<IOType.D1>): Batch<IOType.D1> = input

    override fun IOScope.train(input: Batch<IOType.D1>, label: (Batch<IOType.D1>) -> Batch<IOType.D1>): TResult<IOType.D1> {
        val diff = input - label(input)
        val condition = diff gt 0f
        val delta = where(condition = condition, onTrue = 1f, onFalse = -1f)
        val loss = where(condition = condition, onTrue = diff, onFalse = -1f * diff)
            .batchAverage().average()
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D1.meanAbsolute() = addOutput(
    output = MeanAbsoluteD1(),
    converter = RawD1(inputI),
)

fun <O> GraphBuilder.Node.D1.meanAbsolute(converter: GraphBuilder.Node.D1.() -> Converter.D1<O>) = addOutput(
    output = MeanAbsoluteD1(),
    converter = converter(),
)
