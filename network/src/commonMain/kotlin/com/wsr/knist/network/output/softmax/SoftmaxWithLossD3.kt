package com.wsr.knist.network.output.softmax

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
internal class SoftmaxWithLossD3 internal constructor(val outputI: Int, val outputJ: Int, val outputK: Int, val temperature: Float) : Output.D3() {
    override fun IOScope.expect(input: Batch<IOType.D3>): Batch<IOType.D3> {
        val input = input / temperature
        return input.softmax()
    }

    override fun IOScope.train(input: Batch<IOType.D3>, label: (Batch<IOType.D3>) -> Batch<IOType.D3>): TResult<IOType.D3> {
        val input = input / temperature
        val output = input.softmax()

        val label = label(output)
        val mask = label.sum() gt 0f

        // -log(p)
        val losses = -1f * (output * label).sum().ln(1e-7f)
        val maskedLosses = losses * mask

        // 有効値のみの平均を取る
        val loss = (maskedLosses / mask).batchAverage()

        val delta = (output - label) * mask / temperature
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D3.softmaxWithLoss(axis: Int? = null, temperature: Float = 1f): GraphBuilder.Result.Sink1<Batch<IOType.D3>> {
    val output = when (axis) {
        null -> SoftmaxWithLossD3(
            outputI = inputI,
            outputJ = inputJ,
            outputK = inputK,
            temperature = temperature,
        )

        0, 1, 2 -> SoftmaxWithLossAxisD3(
            outputI = inputI,
            outputJ = inputJ,
            outputK = inputK,
            axis = axis,
            temperature = temperature,
        )

        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addOutput(
        output = output,
        converter = RawD3(inputI, inputJ, inputK),
    )
}

fun <O> GraphBuilder.Node.D3.softmaxWithLoss(axis: Int? = null, temperature: Float = 1f, converter: GraphBuilder.Node.D3.() -> Converter.D3<O>): GraphBuilder.Result.Sink1<O> {
    val output = when (axis) {
        null -> SoftmaxWithLossD3(
            outputI = inputI,
            outputJ = inputJ,
            outputK = inputK,
            temperature = temperature,
        )

        0, 1, 2 -> SoftmaxWithLossAxisD3(
            outputI = inputI,
            outputJ = inputJ,
            outputK = inputK,
            axis = axis,
            temperature = temperature,
        )

        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addOutput(
        output = output,
        converter = converter(),
    )
}
