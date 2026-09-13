package com.wsr.knist.network.output.sigmoid

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
internal class SigmoidWithLossD1 internal constructor(val outputI: Int) : Output.D1() {
    override fun IOScope.expect(input: Batch<IOType.D1>): Batch<IOType.D1> = input.sigmoid()

    override fun IOScope.train(input: Batch<IOType.D1>, label: (Batch<IOType.D1>) -> Batch<IOType.D1>): TResult<IOType.D1> {
        val output = input.sigmoid()
        val label = label(output)
        val one = Batch.d1(label.size, outputI) { 1f }
        val loss = run {
            val y = label * output.ln(1e-7f)
            val p = (one - label) * (one - output).ln(1e-7f)
            -(y + p).sum().batchAverage()
        }
        val delta = output - label
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D1.sigmoidWithLoss() = addOutput(
    output = SigmoidWithLossD1(inputI),
    converter = RawD1(inputI),
)

fun <O> GraphBuilder.Node.D1.sigmoidWithLoss(converter: GraphBuilder.Node.D1.() -> Converter.D1<O>) = addOutput(
    output = SigmoidWithLossD1(inputI),
    converter = converter(),
)
