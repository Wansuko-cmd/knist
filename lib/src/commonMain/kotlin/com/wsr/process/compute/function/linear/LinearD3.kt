package com.wsr.process.compute.function.linear

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class LinearD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Compute.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> = calcDelta(input)
}

fun <T> NetworkBuilder.D3<T>.linear() = addProcess(
    process = LinearD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
