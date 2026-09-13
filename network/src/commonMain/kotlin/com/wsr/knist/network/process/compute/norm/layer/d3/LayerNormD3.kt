package com.wsr.knist.network.process.compute.norm.layer.d3

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
class LayerNormD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, private val e: Float, override val id: String = Uuid.random().toString()) :
    Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    private val outputSize = outputI * outputJ * outputK

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        return numerator / denominator
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        val output = numerator / denominator
        val delta = calcDelta(output)

        // dy/[x-average(x)]
        val dNumerator = output / denominator

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx
        val dx2 = -dNumerator.sum() / outputSize.toFloat()

        // dy/x <- variance(x)のx
        val dx3 = run {
            /**
             * dy/[sqrt(variance(x)]
             *   = (sum(delta * numerator) / denominator) * (-1 / (2f * denominator^2))
             *   = -sum(delta * numerator / denominator) / 2f * denominator^2
             *   = -sum(delta * output) / denominator^2
             *
             * d[sqrt(variance(x)]/[variance(x)] = 1 / outputSize
             *
             * dy/[variance(x)]
             *   = -sum(delta * output) / (denominator^2 * outputSize)
             */
            val dvn = -(delta * output).sum()
            val dvd = 2f * denominator.pow(2) * outputSize.toFloat()
            val dVariance = dvn / dvd

            // dy/[x-average(x)]
            val dSquared = 2f * dVariance * numerator

            // dy/[x]
            val dx1 = dSquared
            // dy/[-average(x)]
            val dx2 = -dSquared.sum() / outputSize.toFloat()

            dx1 + dx2
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}

fun GraphBuilder.Node.D3.layerNorm(axis: Int? = null, e: Float = 1e-6f, id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    val process = when (axis) {
        null -> LayerNormD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            e = e,
            id = id,
        )

        0, 1, 2 -> LayerNormAxisD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
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
