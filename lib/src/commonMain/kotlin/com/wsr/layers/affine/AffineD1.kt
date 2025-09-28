package com.wsr.layers.affine

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.d1.toD2
import com.wsr.d2.dot
import com.wsr.d2.minus
import com.wsr.d2.times
import com.wsr.d2.transpose
import com.wsr.layers.Process
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    override val outputSize: Int,
    private val rate: Double,
    private var weight: IOType.D2,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = forward(input)

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = weight.dot(delta)
        val dw = input.toD2().transpose().dot(delta.toD2())
        weight -= rate / input.size * dw
        return dx
    }

    private fun forward(input: List<IOType.D1>): List<IOType.D1> = weight.transpose().dot(input)
}

fun <T : IOType> NetworkBuilder.D1<T>.affine(neuron: Int) =
    addLayer(
        layer = AffineD1(
            outputSize = neuron,
            rate = rate,
            weight = IOType.d2(inputSize, neuron) { _, _ -> random.nextDouble(-1.0, 1.0) },
        ),
    )