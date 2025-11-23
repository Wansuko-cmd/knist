package com.wsr.layer.process.function.sigmoid

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.math.sigmoid
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.times.times
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class SigmoidD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input.sigmoid()

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = input.sigmoid()
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }
}

fun <T> NetworkBuilder.D1<T>.sigmoid() = addProcess(SigmoidD1(outputSize = inputSize))
