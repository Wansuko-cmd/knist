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
class SwishD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input.toList().map { input ->
        IOType.d2(outputX, outputY) { x, y -> input[x, y] / (1 + exp(-input[x, y])) }
    }.toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val input = input.toList()
        val sigmoid =
            input.map { input ->
                IOType.d2(outputX, outputY) { x, y -> 1 / (1 + exp(-input[x, y])) }
            }
        val output =
            List(input.size) { i ->
                IOType.d2(
                    outputX,
                    outputY,
                ) { x, y -> input[i][x, y] * sigmoid[i][x, y] }
            }
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y ->
                (output[i][x, y] + sigmoid[i][x, y] * (1 - output[i][x, y])) * delta[i][x, y]
            }
        }.toBatch()
    }
}

fun <T> NetworkBuilder.D2<T>.swish() = addProcess(SwishD2(outputX = inputX, outputY = inputY))
