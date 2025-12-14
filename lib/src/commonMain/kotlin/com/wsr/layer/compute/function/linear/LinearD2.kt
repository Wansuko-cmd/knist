package com.wsr.layer.compute.function.linear

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class LinearD2 internal constructor(override val outputX: Int, override val outputY: Int) : Compute.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> = calcDelta(input)
}

fun <T> NetworkBuilder.D2<T>.linear() = addProcess(
    process = LinearD2(outputX = inputX, outputY = inputY),
)
