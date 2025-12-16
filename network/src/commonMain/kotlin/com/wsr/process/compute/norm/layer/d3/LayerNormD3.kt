package com.wsr.process.compute.norm.layer.d3

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.get
import com.wsr.batch.math.pow
import com.wsr.batch.math.sqrt
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.core.collection.average.average
import com.wsr.core.collection.sum.sum
import com.wsr.core.math.pow
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.times.times
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class LayerNormD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val e: Float,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Compute.D3() {
    private val outputSize = outputX * outputY * outputZ

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        return weight * (numerator / denominator)
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

        val normalize = numerator / denominator
        val output = weight * normalize
        val delta = calcDelta(output)

        val dOutput = delta * weight

        weight = optimizer.adapt(
            weight = weight,
            dw = normalize * delta,
        )

        // dy/[x-average(x)]
        val dNumerator = dOutput / denominator

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx
        val dx2 = -1f * dNumerator.sum() / outputSize.toFloat()

        // dy/x <- variance(x)のx
        val dx3 = run {
            /**
             * dy/[sqrt(variance(x)]
             *   = (sum(dOutput * numerator) / denominator) * (-1 / (2f * denominator^2))
             *   = -sum(dOutput * numerator / denominator) / 2f * denominator^2
             *   = -sum(dOutput * normalize) / denominator^2
             *
             * d[sqrt(variance(x)]/[variance(x)] = 1 / outputSize
             *
             * dy/[variance(x)]
             *   = -sum(dOutput * normalize) / (denominator^2 * outputSize)
             */
            val dvn = -1f * (dOutput * normalize).sum()
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
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
): NetworkBuilder.D3<T> {
    val process = when (axis) {
        null -> LayerNormD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
            e = e,
            optimizer = optimizer.d3(inputX, inputY, inputZ),
            weight = initializer.d3(
                input = listOf(inputX, inputY, inputZ),
                output = listOf(inputX, inputY, inputZ),
                x = inputX,
                y = inputY,
                z = inputZ,
            ),
        )

        0, 1, 2 -> LayerNormAxisD3(
            outputX = inputX,
            outputY = inputY,
            outputZ = inputZ,
            optimizer = optimizer.d3(inputX, inputY, inputZ),
            axis = axis,
            e = e,
            weight = initializer.d3(
                input = listOf(inputX, inputY, inputZ),
                output = listOf(inputX, inputY, inputZ),
                x = inputX,
                y = inputY,
                z = inputZ,
            ),
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
