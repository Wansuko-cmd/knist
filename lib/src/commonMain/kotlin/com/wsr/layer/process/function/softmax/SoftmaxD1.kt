package com.wsr.layer.process.function.softmax

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.math.softmax
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input.softmax()

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = input.softmax()
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }
}

fun <T> NetworkBuilder.D1<T>.softmax() = addProcess(SoftmaxD1(outputSize = inputSize))
