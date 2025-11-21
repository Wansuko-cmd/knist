package com.wsr.layer.process.affine

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.dot.matmul.matMul
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.optimizer.Optimizer
import com.wsr.reshape.transpose
import com.wsr.toBatch
import com.wsr.toList
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

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output).toList()

        val dx = delta.matMul(weight.transpose())
        val dw = input.toList().transpose().matMul(delta)

        weight = optimizer.adapt(weight = weight, dw = dw.toBatch())
        return dx.toBatch()
    }

    private fun forward(input: Batch<IOType.D2>): Batch<IOType.D2> = input.toList().matMul(weight).toBatch()
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
