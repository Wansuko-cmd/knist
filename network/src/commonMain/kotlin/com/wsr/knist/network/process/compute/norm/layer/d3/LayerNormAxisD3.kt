package com.wsr.knist.network.process.compute.norm.layer.d3

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unaryMinus
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxisD3 internal constructor(
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

    init {
        require(axis in 0..2) { "LayerNormAxisD3 axis is $axis, not 0, 1 or 2." }
    }

    private val axis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    private val axis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis1 = axis1, axis2 = axis2)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        return numerator.div(other = denominator, axis1 = axis1, axis2 = axis2)
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis1 = axis1, axis2 = axis2)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val output = numerator.div(other = denominator, axis1 = axis1, axis2 = axis2)
        val delta = calcDelta(output)

        val dNumerator = delta.div(other = denominator, axis1 = axis1, axis2 = axis2)

        val dx1 = dNumerator
        val dx2 = -dNumerator.average(axis = axis)
        val dx3 = (-output * dNumerator).average(axis = axis)
            .let { output.times(other = it, axis1 = axis1, axis2 = axis2) }

        return dx1.plus(dx2, axis1 = axis1, axis2 = axis2) + dx3
    }
}
