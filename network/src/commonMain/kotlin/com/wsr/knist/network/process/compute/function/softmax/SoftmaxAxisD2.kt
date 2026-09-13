package com.wsr.knist.network.process.compute.function.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxAxisD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val axis: Int, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    private val axisT = when (axis) {
        0, 1 -> if (axis == 0) 1 else 0
        else -> throw IllegalArgumentException("SoftmaxAxisD2 axis is $axis, not 0 or 1.")
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.softmax(axis = axis)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input.softmax(axis = axis)
        val delta = calcDelta(output)
        val sum = (delta * output).sum(axis = axis)
        return output * delta.minus(other = sum, axis = axisT)
    }
}
