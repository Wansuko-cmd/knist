package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapValue
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class ReLUD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
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
            ) { x, y, z -> if (input[i][x, y, z] >= 0f) delta[i][x, y, z] else 0f }
        }
    }

    private fun forward(input: Batch<IOType.D3>): Batch<IOType.D3> = input.mapValue { if (it >= 0f) it else 0f }
}

fun <T> NetworkBuilder.D3<T>.reLU() = addProcess(
    process = ReLUD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
