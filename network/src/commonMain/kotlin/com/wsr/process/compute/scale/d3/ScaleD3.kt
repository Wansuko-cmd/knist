package com.wsr.process.compute.scale.d3

import com.wsr.batch.Batch
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class ScaleD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Compute.D3() {
    override fun expect(
        input: Batch<IOType.D3>,
        context: Context,
    ): Batch<IOType.D3> = input * weight

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = input * weight
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = input * delta,
        )

        return delta * weight
    }
}
