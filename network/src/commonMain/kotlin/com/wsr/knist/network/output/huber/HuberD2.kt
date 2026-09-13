package com.wsr.knist.network.output.huber

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
internal class HuberD2 internal constructor(val threshold: Float) : Output.D2() {
    override fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2> = input

    override fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val diff = input - label(input)
        val isPositive = diff gt 0f
        val abs = where(condition = isPositive, onTrue = diff, onFalse = -1f * diff)
        val sign = where(condition = isPositive, onTrue = 1f, onFalse = -1f)

        val condition = abs lt threshold
        val delta = where(condition = condition, onTrue = diff, onFalse = threshold * sign)
        val loss = where(
            condition = condition,
            onTrue = 0.5f * diff.pow(2),
            onFalse = threshold * (abs - 0.5f * threshold),
        ).batchAverage().average()
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D2.huber(threshold: Float = 1f) = addOutput(
    output = HuberD2(threshold),
    converter = RawD2(inputI, inputJ),
)

fun <O> GraphBuilder.Node.D2.huber(threshold: Float = 1f, converter: GraphBuilder.Node.D2.() -> Converter.D2<O>) = addOutput(
    output = HuberD2(threshold),
    converter = converter(),
)
