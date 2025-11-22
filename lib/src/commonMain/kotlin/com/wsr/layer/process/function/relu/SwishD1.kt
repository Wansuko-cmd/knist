package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SwishD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input.map { input ->
        IOType.d1(outputSize) { input[it] / (1 + exp(-input[it])) }
    }

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val sigmoid = input.map { input -> IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) } }
        val output = Batch(input.size) { i -> IOType.d1(outputSize) { input[i][it] * sigmoid[i][it] } }
        val delta = calcDelta(output)
        return Batch(input.size) { i ->
            IOType.d1(outputSize) {
                (output[i][it] + sigmoid[i][it] * (1 - output[i][it])) * delta[i][it]
            }
        }
    }
}

fun <T> NetworkBuilder.D1<T>.swish() = addProcess(SwishD1(outputSize = inputSize))
