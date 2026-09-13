package com.wsr.knist.network.output.sigmoid

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
internal class SigmoidWithLossD3 internal constructor(val outputI: Int, val outputJ: Int, val outputK: Int) : Output.D3() {
    override fun IOScope.expect(input: Batch<IOType.D3>): Batch<IOType.D3> = input.sigmoid()

    override fun IOScope.train(input: Batch<IOType.D3>, label: (Batch<IOType.D3>) -> Batch<IOType.D3>): TResult<IOType.D3> {
        val output = input.sigmoid()
        val label = label(output)
        val one = Batch.d3(label.size, outputI, outputJ, outputK) { _, _, _ -> 1f }
        val loss = run {
            val y = label * output.ln(1e-7f)
            val p = (one - label) * (one - output).ln(1e-7f)
            0f - (y + p).sum().batchAverage()
        }
        val delta = output - label
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D3.sigmoidWithLoss() = addOutput(
    output = SigmoidWithLossD3(
        outputI = inputI,
        outputJ = inputJ,
        outputK = inputK,
    ),
    converter = RawD3(inputI, inputJ, inputK),
)

fun <O> GraphBuilder.Node.D3.sigmoidWithLoss(converter: GraphBuilder.Node.D3.() -> Converter.D3<O>) = addOutput(
    output = SigmoidWithLossD3(
        outputI = inputI,
        outputJ = inputJ,
        outputK = inputK,
    ),
    converter = converter(),
)
