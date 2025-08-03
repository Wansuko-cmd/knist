package com.wsr.layers.bias

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class BiasD2(
    override val outputX: Int,
    override val outputY: Int,
    private val rate: Double,
    private val weight: IOType.D2,
) : Layer.D2() {
    override fun expect(input: IOType.D2): IOType.D2 {
        return IOType.D2(outputX, outputY) { x, y -> input[x, y] + weight[x, y] }
    }

    override fun train(
        input: IOType.D2,
        delta: (IOType.D2) -> IOType.D2,
    ): IOType.D2 {
        val output = IOType.D2(outputX, outputY) { x, y -> input[x, y] + weight[x, y] }
        val delta = delta(output)
        for (x in 0 until outputX) {
            for (y in 0 until outputY) {
                weight[x, y] -= rate * delta[x, y]
            }
        }
        return delta
    }
}

fun <T: IOType> NetworkBuilder.D2<T>.bias() = addLayer(
    layer = BiasD2(
        outputX = inputX,
        outputY = inputY,
        rate = rate,
        weight = IOType.D2(inputX, inputY) { _, _ -> random.nextDouble(-1.0, 1.0) }
    )
)
