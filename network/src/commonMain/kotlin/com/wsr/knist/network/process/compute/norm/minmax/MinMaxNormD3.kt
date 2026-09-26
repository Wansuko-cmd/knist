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
class MinMaxNormD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val min = input.min()
        val max = input.max()
        val denominator = max - min
        return (input - min) / denominator
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val min = input.min()
        val max = input.max()

        val numerator = input - min
        val denominator = 1f / (max - min)

        val output = denominator * numerator
        val delta = calcDelta(output)

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = denominator.pow(2) * (numerator * delta).sum()

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

fun GraphBuilder.Node.D3.minMaxNorm(axis: Int? = null, id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    val process = when (axis) {
        null -> MinMaxNormD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            id = id,
        )

        0, 1, 2 -> MinMaxNormAxisD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            axis = axis,
            id = id,
        )

        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addCompute(compute = process)
}
