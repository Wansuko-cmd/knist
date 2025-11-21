package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
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
        val output = forward(input).toList()
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d1(outputSize) {
                delta[i][it] * output[i][it] *
                    (1 - output[i][it])
            }
        }.toBatch()
    }

    private fun forward(input: Batch<IOType.D1>) = input.toList().map { (value) ->
        val max = value.max()
        val exp = value.map { exp(it - max) }
        val sum = exp.sum()
        IOType.d1(outputSize) { exp[it] / sum }
    }.toBatch()
}

fun <T> NetworkBuilder.D1<T>.softmax() = addProcess(SoftmaxD1(outputSize = inputSize))
