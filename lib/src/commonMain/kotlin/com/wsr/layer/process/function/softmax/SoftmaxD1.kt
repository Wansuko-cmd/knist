package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.minus.minus
import com.wsr.batch.times.times
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = forward(input)

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }

    private fun forward(input: Batch<IOType.D1>) = input.map { (value) ->
        val max = value.max()
        val exp = value.map { exp(it - max) }
        val sum = exp.sum()
        IOType.d1(outputSize) { exp[it] / sum }
    }
}

fun <T> NetworkBuilder.D1<T>.softmax() = addProcess(SoftmaxD1(outputSize = inputSize))
