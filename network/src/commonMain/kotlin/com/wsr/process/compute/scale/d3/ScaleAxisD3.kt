package com.wsr.process.compute.scale.d3

import com.wsr.batch.Batch
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class ScaleAxisD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val axis: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Compute.D3() {
    private val sumAxis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    private val sumAxis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }
    override fun expect(
        input: Batch<IOType.D3>,
        context: Context,
    ): Batch<IOType.D3> = input.times(other = weight, axis = axis)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = input.times(other = weight, axis = axis)
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = (input * delta).sum(axis = sumAxis1).sum(axis = sumAxis2),
        )

        return delta.times(other = weight, axis = axis)
    }
}
