package com.wsr.knist.network.output.huber

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
internal class HuberD3 internal constructor(val threshold: Float) : Output.D3() {
    override fun IOScope.expect(input: Batch<IOType.D3>): Batch<IOType.D3> = input

    override fun IOScope.train(input: Batch<IOType.D3>, label: (Batch<IOType.D3>) -> Batch<IOType.D3>): TResult<IOType.D3> {
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

fun GraphBuilder.Node.D3.huber(threshold: Float = 1f) = addOutput(
    output = HuberD3(threshold),
    converter = RawD3(inputI, inputJ, inputK),
)

fun <O> GraphBuilder.Node.D3.huber(threshold: Float = 1f, converter: GraphBuilder.Node.D3.() -> Converter.D3<O>) = addOutput(
    output = HuberD3(threshold),
    converter = converter(),
)
