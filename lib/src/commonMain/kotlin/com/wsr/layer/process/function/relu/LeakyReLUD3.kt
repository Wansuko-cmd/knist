package com.wsr.layer.process.function.relu

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class LeakyReLUD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
) : Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = forward(input)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = forward(input)
        val delta = calcDelta(output)
        return Batch(input.size) { i ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z ->
                if (input[i][x, y, z] >= 0f) delta[i][x, y, z] else 0.01f * delta[i][x, y, z]
            }
        }
    }

    private fun forward(input: Batch<IOType.D3>): Batch<IOType.D3> = input.mapValue { if (it >= 0f) it else 0.01f }
}

fun <T> NetworkBuilder.D3<T>.leakyReLU() = addProcess(
    process = LeakyReLUD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
