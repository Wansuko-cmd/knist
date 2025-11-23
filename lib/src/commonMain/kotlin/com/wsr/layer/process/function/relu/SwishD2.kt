package com.wsr.layer.process.function.relu

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.math.sigmoid
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class SwishD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input * input.sigmoid()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val sigmoid = input.sigmoid()
        val output = input * sigmoid
        val delta = calcDelta(output)
        return (output + sigmoid * (1f - output)) * delta
    }
}

fun <T> NetworkBuilder.D2<T>.swish() = addProcess(SwishD2(outputX = inputX, outputY = inputY))
