package com.wsr.layer.process.norm.layer

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.average
import com.wsr.collection.batchAverage
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
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
class LayerNormD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> {
        val average = input.average().average().average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average().average().average()
        val denominator = variance.map { sqrt(it + 1e-10) }

        return weight * (numerator / denominator)
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val average = input.average().average().average()
        val numerator = input - average

        val variance = numerator.pow(n = 2).average().average().average()
        val denominator = variance.map { sqrt(it + 1e-10) }

        val normalize = numerator / denominator
        val output = weight * normalize
        val delta = calcDelta(output)

        val dOutput = delta.map { it * weight }

        weight = optimizer.adapt(
            weight = weight,
            dw = (normalize * delta).batchAverage(),
        )

        // dy/[x-average(x)]
        val dNumerator = dOutput / denominator

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx
        val dx2 = List(input.size) {
            -dNumerator[it].sum() / (outputX * outputY).toDouble()
        }

        // dy/x <- variance(x)のx
        val dx3: List<IOType.D3> = List(input.size) {
            /**
             * dy/[sqrt(variance(x)]
             *   = (sum(dOutput * numerator) / denominator) * (-1 / (2.0 * denominator^2))
             *   = -sum(dOutput * numerator / denominator) / 2.0 * denominator^2
             *   = -sum(dOutput * normalize) / denominator^2
             *
             * d[sqrt(variance(x)]/[variance(x)] = 1 / outputSize
             *
             * dy/[variance(x)]
             *   = -sum(dOutput * normalize) / (denominator^2 * outputSize)
             */
            val dvn = -(dOutput[it] * normalize[it]).sum()
            val dvd = 2.0 * denominator[it].pow(2) * (outputX * outputY).toDouble()
            val dVariance = dvn / dvd

            // dy/[x-average(x)]
            val dSquared = 2.0 * dVariance * numerator[it]

            // dy/[x]
            val dx1 = dSquared

            // dy/[-average(x)]
            val dx2 = -dSquared.sum() / (outputX * outputY).toDouble()

            dx1 + dx2
        }
        // dy/dx
        return dx1 + dx2 + dx3
    }
}

fun <T> NetworkBuilder.D3<T>.layerNorm(
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1.0),
) = addProcess(
    process = LayerNormD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        optimizer = optimizer.d3(inputX, inputY, inputZ),
        weight = initializer.d3(
            input = listOf(inputX, inputY, inputZ),
            output = listOf(inputX, inputY, inputZ),
            x = inputX,
            y = inputY,
            z = inputZ,
        ),
    ),
)
