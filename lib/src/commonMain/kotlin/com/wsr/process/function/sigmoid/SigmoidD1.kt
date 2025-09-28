package com.wsr.process.function.sigmoid

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SigmoidD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.map(::forward)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d1(outputSize) { delta[i][it] * output[i][it] * (1 - output[i][it]) }
        }
    }

    private fun forward(input: IOType.D1) = IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) }
}

fun <T : IOType> NetworkBuilder.D1<T>.sigmoid() = addProcess(SigmoidD1(outputSize = inputSize))
