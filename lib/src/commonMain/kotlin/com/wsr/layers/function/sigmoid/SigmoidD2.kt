package com.wsr.layers.function.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layers.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SigmoidD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y -> delta[i][x, y] * output[i][x, y] * (1 - output[i][x, y]) }
        }
    }

    private fun forward(input: IOType.D2): IOType.D2 {
        return IOType.d2(outputX, outputY) { x, y -> 1 / (1 + exp(-input[x, y]))  }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.sigmoid() = addProcess(SigmoidD2(outputX = inputX, outputY = inputY))
