package com.wsr.layers.pool

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class MaxPoolD1 internal constructor(
    val poolSize: Int,
    val channel: Int,
    val inputSize: Int,
) : Layer.D2() {
    override val outputX: Int = channel
    override val outputY: Int = inputSize / poolSize

    init {
        check(inputSize % poolSize == 0)
    }

    override fun expectD2(input: List<IOType.D2>): List<IOType.D2> = input.map(::forward)

    override fun trainD2(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        return List(input.size) { index ->
            IOType.d2(channel, inputSize) { c, i ->
                val o = i / poolSize
                if (input[index][c, i] == output[index][c, o]) delta[index][c, o] else 0.0
            }
        }
    }

    private fun forward(input: IOType.D2): IOType.D2 = IOType.d2(outputX, outputY) { x, y ->
        var max = input[x, y]
        for (i in 1 until poolSize) {
            max = maxOf(max, input[x, y + i])
        }
        max
    }
}

fun <T: IOType> NetworkBuilder.D2<T>.maxPool(size: Int) = addLayer(
    layer = MaxPoolD1(
        poolSize = size,
        channel = inputX,
        inputSize = inputY,
    )
)
