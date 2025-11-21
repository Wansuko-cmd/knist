package com.wsr.layer.process.function.relu

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
class SwishD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input.toList().map { input ->
        IOType.d1(outputSize) { input[it] / (1 + exp(-input[it])) }
    }.toBatch()

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val input = input.toList()
        val sigmoid = input.map { input -> IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) } }
        val output =
            List(input.size) { i -> IOType.d1(outputSize) { input[i][it] * sigmoid[i][it] } }
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d1(outputSize) {
                (output[i][it] + sigmoid[i][it] * (1 - output[i][it])) * delta[i][it]
            }
        }.toBatch()
    }
}

fun <T> NetworkBuilder.D1<T>.swish() = addProcess(SwishD1(outputSize = inputSize))
