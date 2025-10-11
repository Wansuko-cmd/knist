package com.wsr.process.affine

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.dot.dot
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.process.Process
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
        val dx = weight.dot(delta)
        val dw = input.toD2().transpose().dot(delta.toD2())
        weight -= optimizer.adapt(dw / input.size.toDouble())
        return dx
    }

    private fun forward(input: List<IOType.D1>): List<IOType.D1> = weight.transpose().dot(input)
}

fun <T : IOType> NetworkBuilder.D1<T>.affine(neuron: Int, optimizer: Optimizer = this.optimizer) = addProcess(
    process =
    AffineD1(
        outputSize = neuron,
        optimizer = optimizer.d2(),
        weight = IOType.d2(inputSize, neuron) { _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
