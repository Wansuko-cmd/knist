package com.wsr.knist.network.process.compute.scale.d3

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class ScaleAxisD3 internal constructor(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    private val axis: Int,
    private var optimizer: Optimizer.D1,
    private var weight: IOType.D1.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    private val sumAxis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    private val sumAxis2 = when (axis) {
        0, 1 -> 1
        else -> 0
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input.times(other = weight, axis = axis)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val output = input.times(other = weight, axis = axis)
        val delta = calcDelta(output)

        val dx = delta.times(other = weight, axis = axis)
        weight = optimizer.adapt(
            weight = weight,
            dw = (input * delta).sum(axis = sumAxis1).sum(axis = sumAxis2),
        ).toGlobal()

        return dx
    }

    override fun update(optimizer: Optimizer) {
        val inputT = when (axis) {
            0 -> inputI
            1 -> inputJ
            else -> inputK
        }
        this.optimizer = optimizer.d1(i = inputT)
    }
}
