package com.wsr.process.norm.layer

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.average
import com.wsr.collection.batchAverage
import com.wsr.collection.sum
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.power.pow
import com.wsr.process.Process
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
        val numerator = List(input.size) {
            IOType.d1(input[it].shape) { x -> input[it][x] - average[it] }
        }

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.map { sqrt(it + 1e-10) }

        return List(input.size) {
            weight * (numerator[it] / denominator[it])
        }
    }

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val average = input.average()
        val numerator = List(input.size) { input[it] - average[it] }

        val variance = numerator.pow(n = 2).average()
        val denominator = variance.map { sqrt(it + 1e-10) }

        val normalize = List(input.size) { numerator[it] / denominator[it] }
        val output = List(input.size) { weight * normalize[it] }
        val delta = calcDelta(output)

        val dOutput = delta.map { it * weight }

        weight = optimizer.adapt(
            weight = weight,
            dw = run {
                val normalize = normalize.batchAverage()
                val delta = delta.batchAverage()
                IOType.d1(weight.shape) { x -> normalize[x] * delta[x] }
            },
        )

        // dy/[x-average(x)]
        val dNumerator = List(input.size) { dOutput[it] / denominator[it] }

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx
        val dx2 = List(input.size) {
            -dNumerator[it].sum() / outputSize.toDouble()
        }

        // dy/x <- variance(x)のx
        val dx3: List<IOType.D1> = List(input.size) {
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
            val dvd = (2.0 * denominator[it].pow(2) * outputSize.toDouble())
            val dVariance = dvn / dvd

            // dy/[x-average(x)]
            val dSquared = 2.0 * dVariance * numerator[it]

            // dy/[x]
            val dx1 = dSquared

            // dy/[-average(x)]
            val dx2 = -dSquared.sum() / outputSize.toDouble()

            dx1 + dx2
        }
        // dy/dx
        return dx1 + dx2 + dx3
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.layerNorm(optimizer: Optimizer = this.optimizer) = addProcess(
    process = LayerNormD1(
        outputSize = inputSize,
        optimizer = optimizer.d1(inputSize),
        weight = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)
