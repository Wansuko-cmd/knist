package com.wsr.layers.pool

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class MaxPoolD1(
    val poolSize: Int,
    val channel: Int,
    val inputSize: Int,
) : Layer.D2() {
    override val outputX: Int = channel
    override val outputY: Int = inputSize / poolSize

    override fun expect(input: IOType.D2): IOType.D2 = forward(input)

    init {
        check(inputSize % poolSize == 0)
    }

    override fun train(
        input: IOType.D2,
        delta: (IOType.D2) -> IOType.D2,
    ): IOType.D2 {
        val output = forward(input)
        val delta = delta(output)
        return IOType.D2(channel, inputSize) { c, i ->
            val o = i / poolSize
            if (input[c, i] == output[c, o]) delta[c, o] else 0.0
        }
    }

    private fun forward(input: IOType.D2): IOType.D2 = IOType.D2(outputX, outputY) { x, y ->
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
