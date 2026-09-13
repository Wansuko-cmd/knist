package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unaryMinus
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
internal class MeanAbsoluteD2 internal constructor() : Output.D2() {
    override fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2> = input

    override fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val diff = input - label(input)
        val condition = diff gt 0f
        val delta = where(condition = condition, onTrue = 1f, onFalse = -1f)
        val loss = where(condition = condition, onTrue = diff, onFalse = -diff)
            .batchAverage().average()
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D2.meanAbsolute() = addOutput(
    output = MeanAbsoluteD2(),
    converter = RawD2(inputI, inputJ),
)

fun <O> GraphBuilder.Node.D2.meanAbsolute(converter: GraphBuilder.Node.D2.() -> Converter.D2<O>) = addOutput(
    output = MeanAbsoluteD2(),
    converter = converter(),
)
