package com.wsr.layer.process.affine

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.batchAverage
import com.wsr.dot.matmul.matMul
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable

@Serializable
class AffineD2 internal constructor(
    private val channel: Int,
    private val outputSize: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override val outputX = channel
    override val outputY = outputSize

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = forward(input)

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)

        val dx = delta.matMul(weight.transpose())
        val dw = input.transpose()
            .matMul(delta)
            .batchAverage()

        weight = optimizer.adapt(weight = weight, dw = dw / channel.toDouble())
        return dx
    }

    private fun forward(input: List<IOType.D2>): List<IOType.D2> = input.matMul(weight)
}

fun <T> NetworkBuilder.D2<T>.affine(
    neuron: Int,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
) = addProcess(
    process =
    AffineD2(
        channel = inputX,
        outputSize = neuron,
        optimizer = optimizer.d2(inputY, neuron),
        weight = initializer.d2(
            input = listOf(inputY),
            output = listOf(neuron),
            x = inputY,
            y = neuron,
        ),
    ),
)
