package com.wsr.process.compute.affine

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.operation.matmul.matMul
import com.wsr.batch.reshape.convert.toD2
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class AffineD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Compute.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = forward(input)

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = weight.matMul(delta)
        val dw = input.toD2().matMul(delta.toD2(), transA = true)
        weight = optimizer.adapt(weight = weight, dw = dw)
        return dx
    }

    private fun forward(input: Batch<IOType.D1>): Batch<IOType.D1> = weight.matMul(input, trans = true)
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
