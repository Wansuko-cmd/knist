package com.wsr.output.sigmoid

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.average.batchAverage
import com.wsr.batch.func.ln
import com.wsr.batch.func.sigmoid
import com.wsr.batch.minus.minus
import com.wsr.batch.plus.plus
import com.wsr.batch.sum.sum
import com.wsr.batch.times.times
import com.wsr.collection.sum
import com.wsr.converter.Converter
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.output.Output
import com.wsr.output.TResult
import com.wsr.power.ln
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
internal class SigmoidWithLossD2 internal constructor(val outputX: Int, val outputY: Int) : Output.D2() {
    override fun expect(input: Batch<IOType.D2>): Batch<IOType.D2> = input

    override fun train(input: Batch<IOType.D2>, label: Batch<IOType.D2>): TResult<IOType.D2> {
        val output = input.sigmoid()
        val one = Batch(label.size) { IOType.d2(outputX, outputY) { _, _ -> 1f } }
        val loss = run {
            val y = label * output.ln(1e-7f)
            val p = (one - label) * (one - output).ln(1e-7f)
            -(y + p).sum().batchAverage().get()
        }
        val delta = output - label
        return TResult(loss = loss, delta = delta)
    }
}

fun <T> NetworkBuilder.D2<T>.sigmoidWithLoss() = addOutput(
    output = SigmoidWithLossD2(
        outputX = inputX,
        outputY = inputY,
    ),
)

fun <I, O> NetworkBuilder.D2<I>.sigmoidWithLoss(converter: NetworkBuilder.D2<I>.() -> Converter.D2<O>) = addOutput(
    output = SigmoidWithLossD2(
        outputX = inputX,
        outputY = inputY,
    ),
    converter = converter,
)
