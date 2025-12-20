package com.wsr.process.compute.norm.layer.d3

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.math.pow
import com.wsr.batch.math.sqrt
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class LayerNormD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val e: Float,
) : Compute.D3() {
    private val outputSize = outputX * outputY * outputZ

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        return numerator / denominator
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
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
        val dx2 = -1f * dNumerator.sum() / outputSize.toFloat()

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
            val dvn = -1f * (delta * output).sum()
            val dvd = 2f * denominator.pow(2) * outputSize.toFloat()
            val dVariance = dvn / dvd

            // dy/[x-average(x)]
            val dSquared = 2f * dVariance * numerator

            // dy/[x]
            val dx1 = dSquared
            // dy/[-average(x)]
            val dx2 = -1f * dSquared.sum() / outputSize.toFloat()

            dx1 + dx2
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}

fun <T> NetworkBuilder.D3<T>.layerNorm(
    axis: Int? = null,
    e: Float = 1e-6f,
): NetworkBuilder.D3<T> {
    val process = when (axis) {
        null -> LayerNormD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
            e = e,
        )

        0, 1, 2 -> LayerNormAxisD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
            axis = axis,
            e = e,
        )

        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addProcess(process = process)
}
