package com.wsr.layer.compute.function.relu

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class ReLUD1 internal constructor(override val outputSize: Int) : Compute.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = forward(input)

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        return Batch(input.size) { i ->
            IOType.d1(outputSize) {
                if (input[i][it] >= 0f) delta[i][it] else 0f
            }
        }
    }

    private fun forward(input: Batch<IOType.D1>): Batch<IOType.D1> = input.mapValue { if (it >= 0f) it else 0f }
}

fun <T> NetworkBuilder.D1<T>.reLU() = addProcess(ReLUD1(outputSize = inputSize))
