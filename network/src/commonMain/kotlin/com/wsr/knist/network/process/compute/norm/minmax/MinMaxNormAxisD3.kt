package com.wsr.knist.network.process.compute.norm.minmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormAxisD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, private val axis: Int, override val id: String = Uuid.random().toString()) :
    Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK

    init {
        check(axis in 0..2) {
            """
            invalid parameter.
            axis: $axis
            """.trimIndent()
        }
    }

    // 四則演算用
    private val axis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    private val axis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val min = input.min(axis = axis)
        val max = input.max(axis = axis)
        val numerator = input.minus(other = min, axis1 = axis1, axis2 = axis2)
        val denominator = max - min
        return numerator.div(other = denominator, axis1 = axis1, axis2 = axis2)
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val min = input.min(axis = axis)
        val max = input.max(axis = axis)
        val numerator = input.minus(other = min, axis1 = axis1, axis2 = axis2)
        val denominator = 1f / (max - min)

        val output = numerator.times(other = denominator, axis1 = axis1, axis2 = axis2)
        val delta = calcDelta(output)

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = denominator.pow(2) * (numerator * delta).sum(axis = axis)

        // 分子側(dy/d[x - min(x)])
        val dNumerator = delta.times(other = denominator, axis1 = axis1, axis2 = axis2)

        val dMin = dNumerator.plus(other = dDenominator - denominator * delta.sum(axis = axis), axis1 = axis1, axis2 = axis2)
        val dMax = dNumerator.minus(other = dDenominator, axis1 = axis1, axis2 = axis2)
        return where(
            condition = numerator eq 0f,
            onTrue = dMin,
            onFalse = where(
                condition = input.minus(other = max, axis1 = axis1, axis2 = axis2) eq 0f,
                onTrue = dMax,
                onFalse = dNumerator,
            ),
        )
    }
}
