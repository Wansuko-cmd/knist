package com.wsr.knist.network.process.compute.norm.minmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD1 internal constructor(override val inputI: Int, override val id: String = Uuid.random().toString()) : Compute.D1() {
    override val outputI: Int get() = inputI
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> {
        val min = input.min()
        val max = input.max()
        val denominator = max - min
        return (input - min) / denominator
    }

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val min = input.min()
        val max = input.max()

        val numerator = input - min
        val denominator = 1f / (max - min)

        val output = denominator * numerator
        val delta = calcDelta(output)

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = denominator.pow(2) * numerator.inner(other = delta)

        // 分子側(dy/d[x - min(x)])
        val dNumerator = denominator * delta

        val dMin = dNumerator + dDenominator - denominator * delta.sum()
        val dMax = dNumerator - dDenominator
        return where(
            condition = (input - min) eq 0f,
            onTrue = dMin,
            onFalse = where(
                condition = (input - max) eq 0f,
                onTrue = dMax,
                onFalse = dNumerator,
            ),
        )
    }
}

fun GraphBuilder.Node.D1.minMaxNorm(id: String = Uuid.random().toString()) = addCompute(
    compute = MinMaxNormD1(
        inputI = inputI,
        id = id,
    ),
)
