package com.wsr.layers.bias

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class BiasD1 internal constructor(
    override val outputSize: Int,
    private val rate: Double,
    private val weight: IOType.D1,
) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> =
        List(input.size) { index -> IOType.d1(outputSize) { input[index][it] + weight[it] } }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = List(input.size) { index -> IOType.d1(outputSize) { input[index][it] + weight[it] } }
        val delta = calcDelta(output)
        for (i in 0 until outputSize) {
            weight[i] -= rate * delta.sumOf { it[i] }
        }
        return delta
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.bias() = addLayer(
    BiasD1(
        outputSize = inputSize,
        rate = rate,
        weight = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)