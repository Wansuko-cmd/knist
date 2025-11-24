package com.wsr.output.softmax

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.get
import com.wsr.batch.math.ln
import com.wsr.batch.math.softmax
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.times.times
import com.wsr.converter.Converter
import com.wsr.core.IOType
import com.wsr.core.collection.sum.sum
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.reshape.broadcastToD2
import com.wsr.output.Output
import com.wsr.output.TResult
import kotlinx.serialization.Serializable

@Serializable
internal class SoftmaxWithLossD2 internal constructor(
    val outputX: Int,
    val outputY: Int,
    val temperature: Float,
    val maskValue: Int? = null,
) : Output.D2() {
    override fun expect(input: Batch<IOType.D2>): Batch<IOType.D2> {
        val input = input / temperature
        return input.softmax(axis = 1)
    }

    override fun train(input: Batch<IOType.D2>, label: Batch<IOType.D2>): TResult<IOType.D2> {
        val input = input / temperature
        val label = label
        val output = input.softmax(axis = 1)
        val mask = label.generateMask()

        // -log(p)
        val losses = -1f * (output * label).sum(axis = 1).ln(1e-7f)
        val maskD1 = mask.sum(axis = 1)
        val maskedLosses = losses * maskD1

        // 有効値のみの平均を取る
        val loss = List(input.size) { maskedLosses[it].sum() / maskD1[it].sum() }
            .average()
            .toFloat()

        val delta = (output - label) * mask
        return TResult(loss = loss, delta = delta)
    }

    private fun Batch<IOType.D2>.generateMask(): Batch<IOType.D2> = when {
        maskValue == null -> Batch(size) { IOType.d2(shape = shape) { _, _ -> 1f } }

        else -> map { label ->
            IOType
                .d1(outputX) { seqId ->
                    val isPadding = label[seqId, maskValue] == 1f
                    if (isPadding) 0f else 1f
                }
                .broadcastToD2(1, outputY)
        }
    }
}

fun <T> NetworkBuilder.D2<T>.softmaxWithLoss(temperature: Float = 1f, maskValue: Int? = null) = addOutput(
    output = SoftmaxWithLossD2(
        outputX = inputX,
        outputY = inputY,
        temperature = temperature,
        maskValue = maskValue,
    ),
)

fun <I, O> NetworkBuilder.D2<I>.softmaxWithLoss(
    converter: NetworkBuilder.D2<I>.() -> Converter.D2<O>,
    temperature: Float = 1f,
    maskValue: Int? = null,
) = addOutput(
    output = SoftmaxWithLossD2(
        outputX = inputX,
        outputY = inputY,
        temperature = temperature,
        maskValue = maskValue,
    ),
    converter = converter,
)
