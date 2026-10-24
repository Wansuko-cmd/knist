package com.wsr.knist.network.process.compute.norm.layer.d2

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unaryMinus
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class LayerNormD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val e: Float, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        return numerator / denominator
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        val output = numerator / denominator
        val delta = calcDelta(output)

        val dNumerator = delta / denominator

        val dx1 = dNumerator
        val dx2 = -dNumerator.average()
        val dx3 = (-output * dNumerator).average() * output

        return dx1 + dx2 + dx3
    }
}

fun GraphBuilder.Node.D2.layerNorm(axis: Int? = null, e: Float = 1e-6f, id: String = Uuid.random().toString()): GraphBuilder.Node.D2 {
    val process = when (axis) {
        null -> LayerNormD2(
            inputI = inputI,
            inputJ = inputJ,
            e = e,
            id = id,
        )

        0, 1 -> LayerNormAxisD2(
            inputI = inputI,
            inputJ = inputJ,
            axis = axis,
            e = e,
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
