package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.minus.minus
import com.wsr.batch.plus.plus
import com.wsr.batch.times.times
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
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
        val output = input * sigmoid
        val delta = calcDelta(output)
        return (output + sigmoid * (1f - output)) * delta
    }
}

fun <T> NetworkBuilder.D1<T>.swish() = addProcess(SwishD1(outputSize = inputSize))
