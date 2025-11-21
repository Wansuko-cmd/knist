package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SwishD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = input.toList().map { input ->
        IOType.d3(
            i = outputX,
            j = outputY,
            k = outputZ,
        ) { x, y, z -> input[x, y, z] / (1 + exp(-input[x, y, z])) }
    }.toBatch()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val input = input.toList()
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
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z ->
                (output[i][x, y, z] + sigmoid[i][x, y, z] * (1 - output[i][x, y, z])) * delta[i][x, y, z]
            }
        }.toBatch()
    }
}

fun <T> NetworkBuilder.D3<T>.swish() = addProcess(
    process = SwishD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
