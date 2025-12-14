package com.wsr.layer.compute.norm.layer.d2

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
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
import com.wsr.core.d0
import com.wsr.core.math.pow
import com.wsr.core.operation.div.div
import com.wsr.core.operation.plus.plus
import com.wsr.core.operation.times.times
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LayerNormD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val e: Float,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Compute.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.sqrt(e = e)

        return weight * (numerator / denominator)
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
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
        val dx2 = Batch(input.size) { IOType.d0(-dNumerator[it].average()) }

        // dy/x <- variance(x)のx
        val dx3 = Batch(input.size) {
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
            val dvn = -(dOutput[it] * normalize[it]).sum()
            val dvd = 2f * denominator[it].pow(2) * (outputX * outputY).toFloat()
            val dVariance = dvn / dvd

            // dy/[x-average(x)]
            val dSquared = 2f * dVariance * numerator[it]

            // dy/[x]
            val dx1 = dSquared

            // dy/[-average(x)]
            val dx2 = -dSquared.average()

            dx1 + dx2
        }
        // dy/dx
        return dx1 + dx2 + dx3
    }
}

fun <T> NetworkBuilder.D2<T>.layerNorm(
    axis: Int? = null,
    e: Float = 1e-6f,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
): NetworkBuilder.D2<T> {
    val process = when (axis) {
        null -> LayerNormD2(
            outputX = inputX,
            outputY = inputY,
            e = e,
            optimizer = optimizer.d2(inputX, inputY),
            weight = initializer.d2(
                input = listOf(inputX, inputY),
                output = listOf(inputX, inputY),
                x = inputX,
                y = inputY,
            ),
        )

        0 -> LayerNormAxis0D2(
            outputX = inputX,
            outputY = inputY,
            e = e,
            optimizer = optimizer.d2(
                inputX,
                inputY,
            ),
            weight = initializer.d2(
                input = listOf(inputX, inputY),
                output = listOf(inputX, inputY),
                x = inputX,
                y = inputY,
            ),
        )

        1 -> LayerNormAxis1D2(
            outputX = inputX,
            outputY = inputY,
            e = e,
            optimizer = optimizer.d2(
                inputX,
                inputY,
            ),
            weight = initializer.d2(
                input = listOf(inputX, inputY),
                output = listOf(inputX, inputY),
                x = inputX,
                y = inputY,
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
