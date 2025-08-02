package com.wsr.layers.bias

import com.wsr.Network
import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class BiasD1 internal constructor(
    override val numOfInput: Int,
    private val rate: Double,
    private val weight: IOType.D1,
) : Layer.D1() {
    override val numOfOutput = numOfInput
    override fun expect(input: IOType.D1): IOType.D1 {
        return IOType.D1(numOfOutput) { input[it] + weight[it] }
    }

    override fun train(
        input: IOType.D1,
        delta: (IOType.D1) -> IOType.D1,
    ): IOType.D1 {
        val output = IOType.D1(numOfOutput) { input[it] + weight[it] }
        val delta = delta(output)
        for (i in 0 until numOfOutput) {
            weight[i] -= rate * delta[i]
        }
        return delta
    }
}

fun Network.Builder.biasD1() =
    addLayer(
        BiasD1(
            numOfInput = numOfInput,
            rate = rate,
            weight = IOType.D1(numOfInput) { random.nextDouble(-1.0, 1.0) },
        ),
    )

fun <T : IOType> NetworkBuilder.D1<T>.bias() = addLayer(
    BiasD1(
        numOfInput = numOfInput,
        rate = rate,
        weight = IOType.D1(numOfInput) { random.nextDouble(-1.0, 1.0) },
    ),
)