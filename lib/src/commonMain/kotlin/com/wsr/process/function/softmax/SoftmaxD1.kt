package com.wsr.process.function.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = forward(input)

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d1(outputSize) {
                delta[i][it] * output[i][it] *
                    (1 - output[i][it])
            }
        }
    }

    private fun forward(input: List<IOType.D1>) = input.map { (value) ->
        val max = value.max()
        val exp = value.map { exp(it - max) }
        val sum = exp.sum()
        IOType.d1(outputSize) { exp[it] / sum }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.softmax() = addProcess(SoftmaxD1(outputSize = inputSize))
