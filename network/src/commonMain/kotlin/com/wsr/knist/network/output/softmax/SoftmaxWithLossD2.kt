package com.wsr.knist.network.output.softmax

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
internal class SoftmaxWithLossD2 internal constructor(val outputJ: Int, val temperature: Float) : Output.D2() {
    override fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2> {
        val input = input / temperature
        return input.softmax(axis = 1)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val input = input / temperature
        val output = input.softmax(axis = 1)

        val label = label(output)
        val mask = label.sum(axis = 1) gt 0f

        // -log(p)
        val losses = -1f * (output * label).sum(axis = 1).ln(1e-7f)
        val maskedLosses = losses * mask

        // 有効値のみの平均を取る
        val loss = (maskedLosses.sum() / mask.sum()).batchAverage()

        val delta = (output - label).times(other = mask, axis = 0) / temperature
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D2.softmaxWithLoss(temperature: Float = 1f) = addOutput(
    output = SoftmaxWithLossD2(
        outputJ = inputJ,
        temperature = temperature,
    ),
    converter = RawD2(inputI, inputJ),
)

fun <O> GraphBuilder.Node.D2.softmaxWithLoss(temperature: Float = 1f, converter: GraphBuilder.Node.D2.() -> Converter.D2<O>) = addOutput(
    output = SoftmaxWithLossD2(
        outputJ = inputJ,
        temperature = temperature,
    ),
    converter = converter(),
)
