package com.wsr.layer.compute.function.sigmoid

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.math.sigmoid
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class SigmoidD2 internal constructor(override val outputX: Int, override val outputY: Int) : Compute.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.sigmoid()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input.sigmoid()
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }
}

fun <T> NetworkBuilder.D2<T>.sigmoid() = addProcess(SigmoidD2(outputX = inputX, outputY = inputY))
