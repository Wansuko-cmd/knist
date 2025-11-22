package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.minus.minus
import com.wsr.batch.times.times
import com.wsr.collection.sum
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SoftmaxD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }

    private fun forward(input: Batch<IOType.D2>) = input.map { input ->
        val max = input.value.max()
        val exp = IOType.d2(shape = input.shape, value = input.value.map { exp(it - max) })
        val sum = exp.sum()
        IOType.d2(outputX, outputY) { x, y -> exp[x, y] / sum }
    }
}

fun <T> NetworkBuilder.D2<T>.softmax() = addProcess(
    process = SoftmaxD2(outputX = inputX, outputY = inputY),
)
