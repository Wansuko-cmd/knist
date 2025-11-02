package com.wsr.layer.process.affine

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.dot.matmul.matMul
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.toD2
import com.wsr.reshape.toD3
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable

@Serializable
class AffineD2 internal constructor(
    private val channel: Int,
    private val outputSize: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D2() {
    override val outputX = channel
    override val outputY = outputSize

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = forward(input)

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = delta.map { delta -> (0 until channel).map { weight[it].matMul(delta[it]) }.toD2() }
        val dwi = input.toD3().transpose(1, 2, 0)
        val dwd = delta.toD3().transpose(1, 0, 2)
        val dw = (0 until channel).map { dwi[it].matMul(dwd[it]) }.toD3() / input.size.toDouble()
        weight = optimizer.adapt(weight = weight, dw = dw)
        return dx
    }

    private fun forward(input: List<IOType.D2>): List<IOType.D2> {
        val weight = (0 until channel).map { weight[it].transpose() }
        return input.map { input ->
            (0 until channel)
                .map { weight[it].matMul(input[it]) }
                .toD2()
        }
    }
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
        optimizer = optimizer.d3(inputX, inputY, neuron),
        weight = initializer.d3(
            input = listOf(inputY),
            output = listOf(neuron),
            x = inputX,
            y = inputY,
            z = neuron,
        ),
    ),
)
