package com.wsr.layers.affine

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    val inputSize: Int,
    override val outputSize: Int,
    private val rate: Double,
    private val weight: IOType.D2,
) : Layer.D1() {
    override fun expectD1(input: List<IOType.D1>): List<IOType.D1> = input.map(::forward)

    override fun trainD1(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input.map(::forward)
        val delta = calcDelta(output)
        val dx = List(input.size) { i ->
            IOType.d1(inputSize) { inputIndex ->
                var sum = 0.0
                for (outputIndex in 0 until outputSize) {
                    sum += delta[i][outputIndex] * weight[inputIndex, outputIndex]
                }
                sum
            }
        }
        for (inputIndex in 0 until inputSize) {
            for (outputIndex in 0 until outputSize) {
                weight[inputIndex, outputIndex] -= rate * delta.sumOf { it[outputIndex] } * input.sumOf { it[inputIndex] }
            }
        }
        return dx
    }

    private fun forward(input: IOType.D1): IOType.D1 {
        return IOType.d1(outputSize) { outputIndex ->
            var sum = 0.0
            for (inputIndex in 0 until inputSize) {
                sum += input[inputIndex] * weight[inputIndex, outputIndex]
            }
            sum
        }
    }
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