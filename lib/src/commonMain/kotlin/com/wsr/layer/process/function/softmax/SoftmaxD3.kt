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
class SoftmaxD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input.softmax()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = input.softmax()
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }
}

fun <T> NetworkBuilder.D3<T>.softmax() = addProcess(
    process = SoftmaxD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
