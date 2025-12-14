package com.wsr.process.compute.function.softmax

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.math.softmax
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Compute.D3() {
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
