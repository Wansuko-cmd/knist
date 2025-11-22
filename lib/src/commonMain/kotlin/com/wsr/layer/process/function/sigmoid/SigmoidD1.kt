package com.wsr.layer.process.function.sigmoid

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SigmoidD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> =
        input.map(::forward)

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return Batch(input.size) { i ->
            IOType.d1(outputSize) { delta[i][it] * output[i][it] * (1 - output[i][it]) }
        }
    }

    private fun forward(input: IOType.D1) = IOType.d1(outputSize) { 1 / (1 + exp(-input[it])) }
}

fun <T> NetworkBuilder.D1<T>.sigmoid() = addProcess(SigmoidD1(outputSize = inputSize))
