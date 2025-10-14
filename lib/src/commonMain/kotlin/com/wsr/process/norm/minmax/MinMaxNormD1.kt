package com.wsr.process.norm.minmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.batchAverage
import com.wsr.collection.max
import com.wsr.collection.min
import com.wsr.dot.inner.inner
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.process.Process
import kotlin.math.pow
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        val min = input.min()
        val max = input.max()
        return List(input.size) {
            val denominator = max[it] - min[it]
            IOType.d1(outputSize) { x -> weight[x] * (input[it][x] - min[it]) / denominator }
        }
    }

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val min = input.min()
        val max = input.max()

        val numerator = List(input.size) {
            IOType.d1(input[it].shape) { x -> input[it][x] - min[it] }
        }
        val denominator = List(numerator.size) { 1 / (max[it] - min[it]) }

        val mean = List(input.size) { denominator[it] * numerator[it] }
        val output = mean.map { weight * it }

        val delta = calcDelta(output)

        val dOutput = delta.map { it * weight }

        weight = optimizer.adapt(
            weight = weight,
            dw = run {
                val mean = mean.batchAverage()
                val delta = delta.batchAverage()
                IOType.d1(weight.shape) { x -> mean[x] * delta[x] }
            },
        )

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = List(input.size) {
            denominator[it].pow(2) * numerator[it].inner(dOutput[it])
        }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        return List(input.size) {
            IOType.d1(input[it].shape) { x ->
                /**
                 * dy/input + dy/min(x) + dy/max(x)
                 * dy/dx = dNumerator
                 * dy/min(x) = if(x == min(x)) -dNumerator + dDenominator else 0.0
                 * dy/max(x) = if(x == max(x)) -dDenominator else 0.0
                 */
                val input = input[it][x]
                when (input) {
                    min[it] -> dDenominator[it]
                    max[it] -> dNumerator[it][x] - dDenominator[it]
                    else -> dNumerator[it][x]
                }
            }
        }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.minMaxNorm(optimizer: Optimizer = this.optimizer) = addProcess(
    process = MinMaxNormD1(
        outputSize = inputSize,
        optimizer = optimizer.d1(inputSize),
        weight = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)
