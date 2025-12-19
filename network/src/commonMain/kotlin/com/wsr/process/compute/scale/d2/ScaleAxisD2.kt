package com.wsr.process.compute.scale.d2

import com.wsr.batch.Batch
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class ScaleAxisD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val axis: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Compute.D2() {
    private val sumAxis = if (axis == 0) 1 else 0
    override fun expect(
        input: Batch<IOType.D2>,
        context: Context,
    ): Batch<IOType.D2> = input.times(other = weight, axis = axis)

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input.times(other = weight, axis = axis)
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = (input * delta).sum(axis = sumAxis),
        )

        return delta.times(other = weight, axis = axis)
    }
}
