package com.wsr.process.compute.scale.d1

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class ScaleD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Compute.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input * weight

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val output = input * weight
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = input * delta,
        )

        return delta * weight
    }
}

fun <T> NetworkBuilder.D1<T>.scale(optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = Fixed(1f)) =
    addProcess(
        process = ScaleD1(
            outputSize = inputSize,
            optimizer = optimizer.d1(inputSize),
            weight = initializer.d1(
                input = listOf(inputSize),
                output = listOf(inputSize),
                size = inputSize,
            ),
        ),
    )
