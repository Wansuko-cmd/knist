package com.wsr.layer.process.affine

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.dot.matmul.matMul
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.toD2
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = forward(input)

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = weight.matMul(delta)
        val dw = input.toD2().transpose().matMul(delta.toD2())
        weight = optimizer.adapt(weight = weight, dw = dw)
        return dx
    }

    private fun forward(input: List<IOType.D1>): List<IOType.D1> = weight.transpose().matMul(input)
}

fun <T> NetworkBuilder.D1<T>.affine(
    neuron: Int,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
) = addProcess(
    process =
    AffineD1(
        outputSize = neuron,
        optimizer = optimizer.d2(inputSize, neuron),
        weight = initializer.d2(
            input = listOf(inputSize),
            output = listOf(neuron),
            x = inputSize,
            y = neuron,
        ),
    ),
)
