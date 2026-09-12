package com.wsr.knist.network.process.compute.bias.d2

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class BiasAxisD2(
    override val inputI: Int,
    override val inputJ: Int,
    private val axis: Int,
    private var optimizer: Optimizer.D1,
    private var weight: IOType.D1.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    private val sumAxis = if (axis == 0) 1 else 0

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.plus(other = weight, axis = axis)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input.plus(other = weight, axis = axis)
        val delta = calcDelta(output)
        weight = optimizer.adapt(weight = weight, dw = delta.sum(axis = sumAxis)).toGlobal()
        return delta
    }

    override fun update(optimizer: Optimizer) {
        val inputT = if (axis == 0) inputI else inputJ
        this.optimizer = optimizer.d1(i = inputT)
    }
}
