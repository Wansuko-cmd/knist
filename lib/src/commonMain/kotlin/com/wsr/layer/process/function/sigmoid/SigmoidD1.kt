package com.wsr.layer.process.function.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SigmoidD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: List<IOType.D1>, context: Context): List<IOType.D1> = input.map(::forward)

    override fun train(input: List<IOType.D1>, context: Context, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d1(outputSize) { delta[i][it] * output[i][it] * (1 - output[i][it]) }
        }
    }

    private fun forward(input: IOType.D1) = IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) }
}

fun <T> NetworkBuilder.D1<T>.sigmoid() = addProcess(SigmoidD1(outputSize = inputSize))
