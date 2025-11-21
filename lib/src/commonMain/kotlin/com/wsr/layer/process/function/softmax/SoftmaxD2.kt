package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = forward(input).toList()
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y ->
                delta[i][x, y] * output[i][x, y] * (1 - output[i][x, y])
            }
        }.toBatch()
    }

    private fun forward(input: Batch<IOType.D2>) = input.toList().map { input ->
        val max = input.value.max()
        val exp = IOType.d2(shape = input.shape, value = input.value.map { exp(it - max) })
        val sum = exp.sum()
        IOType.d2(outputX, outputY) { x, y -> exp[x, y] / sum }
    }.toBatch()
}

fun <T> NetworkBuilder.D2<T>.softmax() = addProcess(
    process = SoftmaxD2(outputX = inputX, outputY = inputY),
)
