package com.wsr.layer.compute.affine

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.operation.matmul.matMul
import com.wsr.batch.reshape.transpose.transpose
import com.wsr.core.IOType
import com.wsr.core.reshape.transpose.transpose
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class AffineD2 internal constructor(
    private val channel: Int,
    private val outputSize: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Compute.D2() {
    override val outputX = channel
    override val outputY = outputSize

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)

        val dx = delta.matMul(weight.transpose())
        val dw = input.transpose().matMul(delta)

        weight = optimizer.adapt(weight = weight, dw = dw)
        return dx
    }

    private fun forward(input: Batch<IOType.D2>): Batch<IOType.D2> = input.matMul(weight)
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
