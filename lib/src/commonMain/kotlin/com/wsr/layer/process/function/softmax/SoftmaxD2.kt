package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.func.softmax
import com.wsr.batch.minus.minus
import com.wsr.batch.times.times
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.softmax()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input.softmax()
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }
}

fun <T> NetworkBuilder.D2<T>.softmax() = addProcess(
    process = SoftmaxD2(outputX = inputX, outputY = inputY),
)
