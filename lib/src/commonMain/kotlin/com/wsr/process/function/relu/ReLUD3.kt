package com.wsr.process.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class ReLUD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> = input.map(::forward)

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z -> if (input[i][x, y, z] >= 0.0) delta[i][x, y, z] else 0.0 }
        }
    }

    private fun forward(input: IOType.D3): IOType.D3 = IOType.d3(
        i = outputX,
        j = outputY,
        k = outputZ,
    ) { x, y, z -> if (input[x, y, z] >= 0.0) input[x, y, z] else 0.0 }
}

fun <T> NetworkBuilder.D3<T>.reLU() = addProcess(
    process = ReLUD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
