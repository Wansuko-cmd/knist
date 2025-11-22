package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class ReLUD1 internal constructor(override val outputSize: Int) : Process.D1() {
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
            IOType.d1(outputSize) {
                if (input[i][it] >= 0f) delta[i][it] else 0f
            }
        }
    }

    private fun forward(input: IOType.D1): IOType.D1 = IOType.d1(outputSize) {
        if (input[it] >= 0f) input[it] else 0f
    }
}

fun <T> NetworkBuilder.D1<T>.reLU() = addProcess(ReLUD1(outputSize = inputSize))
