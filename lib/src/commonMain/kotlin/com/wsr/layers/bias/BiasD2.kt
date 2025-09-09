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
    override fun expectD2(input: List<IOType.D2>): List<IOType.D2> = List(input.size) {
        IOType.d2(outputX, outputY) { x, y -> input[it][x, y] + weight[x, y] }
    }

    override fun trainD2(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = List(input.size) { IOType.d2(outputX, outputY) { x, y -> input[it][x, y] + weight[x, y] } }
        val delta = calcDelta(output)
        for (x in 0 until outputX) {
            for (y in 0 until outputY) {
                weight[x, y] -= rate * delta.sumOf { it[x, y] }
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
        weight = IOType.d2(inputX, inputY) { _, _ -> random.nextDouble(-1.0, 1.0) }
    )
)
