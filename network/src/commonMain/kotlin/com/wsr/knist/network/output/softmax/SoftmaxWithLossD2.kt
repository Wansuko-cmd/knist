package com.wsr.knist.network.output.softmax

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
internal class SoftmaxWithLossD2 internal constructor(val outputI: Int, val outputJ: Int, val temperature: Float) : Output.D2() {
    override fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2> {
        val input = input / temperature
        return input.softmax()
    }

    override fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val input = input / temperature
        val output = input.softmax()

        val label = label(output)
        val mask = label.sum() gt 0f

        // -log(p)
        val losses = -(output * label).sum().ln(1e-7f)
        val maskedLosses = losses * mask

        // 有効値のみの平均を取る
        val loss = (maskedLosses / mask).batchAverage()

        val delta = (output - label) * mask / temperature
        return TResult(loss = loss, delta = delta)
    }
}

fun GraphBuilder.Node.D2.softmaxWithLoss(axis: Int? = null, temperature: Float = 1f): GraphBuilder.Result.Sink1<Batch<IOType.D2>> {
    val output = when (axis) {
        null -> SoftmaxWithLossD2(
            outputI = inputI,
            outputJ = inputJ,
            temperature = temperature,
        )

        0, 1 -> SoftmaxWithLossAxisD2(
            outputI = inputI,
            outputJ = inputJ,
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
        converter = RawD2(inputI, inputJ),
    )
}

fun <O> GraphBuilder.Node.D2.softmaxWithLoss(axis: Int? = null, temperature: Float = 1f, converter: GraphBuilder.Node.D2.() -> Converter.D2<O>): GraphBuilder.Result.Sink1<O> {
    val output = when (axis) {
        null -> SoftmaxWithLossD2(
            outputI = inputI,
            outputJ = inputJ,
            temperature = temperature,
        )

        0, 1 -> SoftmaxWithLossAxisD2(
            outputI = inputI,
            outputJ = inputJ,
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
