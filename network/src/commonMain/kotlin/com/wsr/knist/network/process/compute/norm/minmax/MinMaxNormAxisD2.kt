package com.wsr.knist.network.process.compute.norm.minmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormAxisD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val axis: Int, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ

    init {
        check(axis == 0 || axis == 1) {
            """
            invalid parameter.
            axis: $axis
            """.trimIndent()
        }
    }

    // 四則演算用
    private val basicOpAxis = if (axis == 0) 1 else 0

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val min = input.min(axis = axis)
        val max = input.max(axis = axis)
        val numerator = input.minus(other = min, axis = basicOpAxis)
        val denominator = max - min
        return numerator.div(other = denominator, axis = basicOpAxis)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val min = input.min(axis = axis)
        val max = input.max(axis = axis)
        val numerator = input.minus(other = min, axis = basicOpAxis)
        val denominator = 1f / (max - min)

        val output = numerator.times(other = denominator, axis = basicOpAxis)
        val delta = calcDelta(output)

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = denominator.pow(2) * (numerator * delta).sum(axis = axis)

        // 分子側(dy/d[x - min(x)])
        val dNumerator = delta.times(other = denominator, axis = basicOpAxis)

        val dMin = dNumerator.plus(other = dDenominator - denominator * delta.sum(axis = axis), axis = basicOpAxis)
        val dMax = dNumerator.minus(other = dDenominator, axis = basicOpAxis)
        return where(
            condition = numerator eq 0f,
            onTrue = dMin,
            onFalse = where(
                condition = input.minus(other = max, axis = basicOpAxis) eq 0f,
                onTrue = dMax,
                onFalse = dNumerator,
            ),
        )
    }
}
