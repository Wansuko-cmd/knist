package com.wsr.layers.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layers.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SwishD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input.map { input ->
        IOType.d1(outputSize) { input[it] / (1 + exp(-input[it])) }
    }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val sigmoid = input.map { input -> IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) } }
        val output = List(input.size) { i -> IOType.d1(outputSize) { input[i][it] * sigmoid[i][it] } }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d1(outputSize) {
                (output[i][it] + sigmoid[i][it] * (1 - output[i][it])) * delta[i][it]
            }
        }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.swish() = addProcess(SwishD1(outputSize = inputSize))
