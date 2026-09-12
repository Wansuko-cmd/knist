package com.wsr.knist.network.process.compute.norm.rms.d3

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class RmsNormAxisD3 internal constructor(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    private val axis: Int,
    private val e: Float,
    override val id: String = Uuid.random().toString(),
) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    private val axis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    private val axis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val deviation = input.pow(n = 2).average(axis = axis).sqrt(e = e)
        return input.div(other = deviation, axis1 = axis1, axis2 = axis2)
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val variance = input.pow(2).average(axis = axis)
        val deviation = variance.sqrt(e = e)
        val output = input.div(other = deviation, axis1 = axis1, axis2 = axis2)

        val delta = calcDelta(output)

        val dx1 = delta.div(other = deviation, axis1 = axis1, axis2 = axis2)
        val dx2 = run {
            val m = (delta * input).average(axis = axis) / deviation.pow(3)
            m.times(other = input, axis1 = axis1, axis2 = axis2)
        }
        return dx1 - dx2
    }
}
