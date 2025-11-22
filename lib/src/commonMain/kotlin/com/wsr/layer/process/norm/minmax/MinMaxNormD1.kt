package com.wsr.layer.process.norm.minmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.div.div
import com.wsr.batch.func.pow
import com.wsr.batch.inner.inner
import com.wsr.batch.minmax.max
import com.wsr.batch.minmax.min
import com.wsr.batch.minus.minus
import com.wsr.batch.times.times
import com.wsr.d1
import com.wsr.get
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import com.wsr.set
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> {
        val min = input.min()
        val max = input.max()
        val denominator = max - min
        return weight * (input - min) / denominator
    }

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val min = input.min()
        val max = input.max()

        val numerator = input - min
        val denominator = 1f / (max - min)

        val mean = denominator * numerator
        val output = weight * mean

        val delta = calcDelta(output)

        val dOutput = delta * weight

        weight = optimizer.adapt(
            weight = weight,
            dw = mean * delta,
        )

        numerator.inner(other = dOutput)

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = denominator.pow(2) * numerator.inner(other = dOutput)

        // 分子側(dy/d[x - min(x)])
        val dNumerator = denominator * dOutput

        return Batch(input.size) {
            val input = input[it]
            val min = min[it]
            val max = max[it]
            val dDenominator = dDenominator[it]
            val dNumerator = dNumerator[it]
            IOType.d1(input.shape) { x ->
                /**
                 * dy/input + dy/min(x) + dy/max(x)
                 * dy/dx = dNumerator
                 * dy/min(x) = if(x == min(x)) -dNumerator + dDenominator else 0f
                 * dy/max(x) = if(x == max(x)) -dDenominator else 0f
                 */
                val input = input[x]
                when (input) {
                    min.get() -> dDenominator.get()
                    max.get() -> dNumerator[x] - dDenominator.get()
                    else -> dNumerator[x]
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
