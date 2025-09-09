package com.wsr.layers.bias

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.common.averageOf
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import com.wsr.common.d1.*

@Serializable
class BiasD1 internal constructor(
    override val outputSize: Int,
    private val rate: Double,
    private var weight: IOType.D1,
) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input + weight

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input + weight
        val delta = calcDelta(output)
        weight -= rate * delta.average()
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