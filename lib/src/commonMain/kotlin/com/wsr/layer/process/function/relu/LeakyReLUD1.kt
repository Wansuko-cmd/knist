package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class LeakyReLUD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input.toList().map(::forward).toBatch()

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val input = input.toList()
        val output = input.map(::forward)
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d1(outputSize) { if (input[i][it] >= 0f) delta[i][it] else 0.01f * delta[i][it] }
        }.toBatch()
    }

    private fun forward(input: IOType.D1): IOType.D1 = IOType.d1(outputSize) {
        if (input[it] >=
            0f
        ) {
            input[it]
        } else {
            0.01f
        }
    }
}

fun <T> NetworkBuilder.D1<T>.leakyReLU() = addProcess(LeakyReLUD1(outputSize = inputSize))
