package com.wsr.layers.affine

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.common.averageOf
import com.wsr.common.d1.average
import com.wsr.common.d2.dot
import com.wsr.common.d2.minus
import com.wsr.common.d2.transpose
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    val inputSize: Int,
    override val outputSize: Int,
    private val rate: Double,
    private var weight: IOType.D2,
) : Layer.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = forward(input)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = weight.dot(delta)
        weight -= IOType.d2(inputSize, outputSize) { x, y -> rate * input.average(x) * delta.average(y) }
        return dx
    }

    private fun forward(input: List<IOType.D1>): List<IOType.D1> = weight.transpose().dot(input)
}

fun <T : IOType> NetworkBuilder.D1<T>.affine(neuron: Int) =
    addLayer(
        layer = AffineD1(
            inputSize = inputSize,
            outputSize = neuron,
            rate = rate,
            weight = IOType.d2(inputSize, neuron) { _, _ -> random.nextDouble(-1.0, 1.0) },
        ),
    )