package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class LeakyReLUD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>, context: Context): List<IOType.D3> = input.map(::forward)

    override fun train(
        input: List<IOType.D3>,
        context: Context,
        calcDelta: (List<IOType.D3>) -> List<IOType.D3>,
    ): List<IOType.D3> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z ->
                if (input[i][x, y, z] >= 0f) delta[i][x, y, z] else 0.01f * delta[i][x, y, z]
            }
        }
    }

    private fun forward(input: IOType.D3): IOType.D3 = IOType.d3(
        i = outputX,
        j = outputY,
        k = outputZ,
    ) { x, y, z ->
        if (input[x, y, z] >= 0f) input[x, y, z] else 0.01f
    }
}

fun <T> NetworkBuilder.D3<T>.leakyReLU() = addProcess(
    process = LeakyReLUD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
