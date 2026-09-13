package com.wsr.knist.network.process.compute.function.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxAxisD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, private val axis: Int, override val id: String = Uuid.random().toString()) :
    Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    private val axis1 = when (axis) {
        0 -> 1
        1, 2 -> 0
        else -> throw IllegalArgumentException("SoftmaxAxisD3 axis is $axis, not 0, 1 or 2.")
    }
    private val axis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input.softmax(axis = axis)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val output = input.softmax(axis = axis)
        val delta = calcDelta(output)
        val sum = (delta * output).sum(axis = axis)
        return output * delta.minus(other = sum, axis1 = axis1, axis2 = axis2)
    }
}
