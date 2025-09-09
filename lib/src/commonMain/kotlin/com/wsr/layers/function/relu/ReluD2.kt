package com.wsr.layers.function.relu

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class ReluD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
) : Layer.D2() {
    override fun expect(input: IOType.D2): IOType.D2 = forward(input)

    override fun train(
        input: IOType.D2,
        calcDelta: (IOType.D2) -> IOType.D2,
    ): IOType.D2 {
        val output = forward(input)
        val delta = calcDelta(output)
        return IOType.d2(outputX, outputY) { x, y -> if (output[x, y] <= 0.0) 0.0 else delta[x, y] }
    }

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return delta.mapIndexed { i, d ->
            IOType.d2(outputX, outputY) { x, y -> if (output[i][x, y] <= 0.0) 0.0 else d[x, y] }
        }
    }

    private fun forward(input: IOType.D2): IOType.D2 {
        return IOType.d2(outputX, outputY) { x, y -> input[x, y].coerceAtLeast(0.0) }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.relu() = addLayer(ReluD2(outputX = inputX, outputY = inputY))
