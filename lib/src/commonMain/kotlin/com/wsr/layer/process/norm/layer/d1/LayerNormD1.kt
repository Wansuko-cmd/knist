package com.wsr.layer.process.norm.layer.d1

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.average
import com.wsr.collection.sum
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.power.pow
import kotlin.math.pow
import kotlin.math.sqrt
import kotlinx.serialization.Serializable

@Serializable
class LayerNormD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.map { sqrt(it + 1e-10f) }

        return weight * (numerator / denominator)
    }

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val average = input.average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.map { sqrt(it + 1e-10f) }

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
        val dx2 = List(input.size) { -dNumerator[it].sum() / outputSize.toFloat() }

        // dy/x <- variance(x)のx
        val dx3: List<IOType.D1> = List(input.size) {
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
            val dvd = (2f * denominator[it].pow(2) * outputSize.toFloat())
            val dVariance = dvn / dvd

            // dy/[x-average(x)]
            val dSquared = 2f * dVariance * numerator[it]

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

fun <T> NetworkBuilder.D1<T>.layerNorm(
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
) = addProcess(
    process = LayerNormD1(
        outputSize = inputSize,
        optimizer = optimizer.d1(inputSize),
        weight = initializer.d1(
            input = listOf(inputSize),
            output = listOf(inputSize),
            size = inputSize,
        ),
    ),
)
