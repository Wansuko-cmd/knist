package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.toD2
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
        val delta = (diff gt 0f) - (diff lt 0f)
        val loss = (diff * delta).toD2().average()
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
