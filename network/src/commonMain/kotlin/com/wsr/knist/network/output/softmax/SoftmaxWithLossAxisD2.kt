package com.wsr.knist.network.output.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unaryMinus
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.output.TResult
import kotlinx.serialization.Serializable

@Serializable
internal class SoftmaxWithLossAxisD2 internal constructor(val outputI: Int, val outputJ: Int, val axis: Int, val temperature: Float) : Output.D2() {
    private val axisT = when (axis) {
        0, 1 -> if (axis == 0) 1 else 0
        else -> throw IllegalArgumentException("SoftmaxWithLossAxisD2 axis is $axis, not 0 or 1.")
    }

    override fun IOScope.expect(input: Batch<IOType.D2>): Batch<IOType.D2> {
        val input = input / temperature
        return input.softmax(axis = axis)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, label: (Batch<IOType.D2>) -> Batch<IOType.D2>): TResult<IOType.D2> {
        val input = input / temperature
        val output = input.softmax(axis = axis)

        val label = label(output)
        val mask = label.sum(axis = axis) gt 0f

        // -log(p)
        val losses = -(output * label).sum(axis = axis).ln(1e-7f)
        val maskedLosses = losses * mask

        // 有効値のみの平均を取る
        val loss = (maskedLosses.sum() / mask.sum()).batchAverage()

        val delta = (output - label).times(other = mask, axis = axisT) / temperature
        return TResult(loss = loss, delta = delta)
    }
}
