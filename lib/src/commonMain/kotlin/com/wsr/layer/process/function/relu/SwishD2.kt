package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SwishD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: List<IOType.D2>, context: Context): List<IOType.D2> = input.map { input ->
        IOType.d2(outputX, outputY) { x, y -> input[x, y] / (1 + exp(-input[x, y])) }
    }

    override fun train(input: List<IOType.D2>, context: Context, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
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
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y ->
                (output[i][x, y] + sigmoid[i][x, y] * (1 - output[i][x, y])) * delta[i][x, y]
            }
        }
    }
}

fun <T> NetworkBuilder.D2<T>.swish() = addProcess(SwishD2(outputX = inputX, outputY = inputY))
