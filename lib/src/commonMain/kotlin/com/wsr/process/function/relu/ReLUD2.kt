package com.wsr.process.function.relu

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class ReLUD2 internal constructor(override val outputX: Int, override val outputY: Int) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(
                outputX,
                outputY,
            ) { x, y -> if (input[i][x, y] >= 0.0) delta[i][x, y] else 0.0 }
        }
    }

    private fun forward(input: IOType.D2): IOType.D2 = IOType.d2(outputX, outputY) { x, y ->
        if (input[x, y] >=
            0.0
        ) {
            input[x, y]
        } else {
            0.0
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.reLU() = addProcess(ReLUD2(outputX = inputX, outputY = inputY))
