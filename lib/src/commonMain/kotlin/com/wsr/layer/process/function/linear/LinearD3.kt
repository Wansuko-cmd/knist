package com.wsr.layer.process.function.linear

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class LinearD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
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
