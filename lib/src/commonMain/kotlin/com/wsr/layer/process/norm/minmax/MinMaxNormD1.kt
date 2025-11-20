package com.wsr.layer.process.norm.minmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.max
import com.wsr.collection.min
import com.wsr.dot.inner.inner
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import kotlin.math.pow
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>, context: Context): List<IOType.D1> {
        val min = input.min()
        val max = input.max()
        return List(input.size) {
            val denominator = max[it] - min[it]
            IOType.d1(outputSize) { x -> weight[x] * (input[it][x] - min[it]) / denominator }
        }
    }

    override fun train(
        input: List<IOType.D1>,
        context: Context,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
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
            dw = mean * delta,
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
                 * dy/min(x) = if(x == min(x)) -dNumerator + dDenominator else 0f
                 * dy/max(x) = if(x == max(x)) -dDenominator else 0f
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

fun <T> NetworkBuilder.D1<T>.minMaxNorm(
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
) = addProcess(
    process = MinMaxNormD1(
        outputSize = inputSize,
        optimizer = optimizer.d1(inputSize),
        weight = initializer.d1(
            input = listOf(inputSize),
            output = listOf(inputSize),
            size = inputSize,
        ),
    ),
)
