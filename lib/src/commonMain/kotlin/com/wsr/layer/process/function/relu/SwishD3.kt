package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.process.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SwishD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> = input.map { input ->
        IOType.d3(
            i = outputX,
            j = outputY,
            k = outputZ,
        ) { x, y, z -> input[x, y, z] / (1 + exp(-input[x, y, z])) }
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val sigmoid = input.map { input ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z -> 1 / (1 + exp(-input[x, y, z])) }
        }
        val output = List(input.size) { i ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z -> input[i][x, y, z] * sigmoid[i][x, y, z] }
        }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z ->
                (output[i][x, y, z] + sigmoid[i][x, y, z] * (1 - output[i][x, y, z])) * delta[i][x, y, z]
            }
        }
    }
}

fun <T> NetworkBuilder.D3<T>.swish() = addProcess(
    process = SwishD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
