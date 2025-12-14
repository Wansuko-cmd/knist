package com.wsr.layer.compute.norm.minmax

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.minmax.max
import com.wsr.batch.collecction.minmax.min
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.get
import com.wsr.batch.math.pow
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Compute.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val min = input.min()
        val max = input.max()
        val denominator = max - min
        return weight * (input - min) / denominator
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
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

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = denominator.pow(2) * (numerator * dOutput).sum()

        // 分子側(dy/d[x - min(x)])
        val dNumerator = denominator * dOutput

        return Batch(input.size) {
            val input = input[it]
            val min = min[it]
            val max = max[it]
            val dDenominator = dDenominator[it]
            val dNumerator = dNumerator[it]
            IOType.d2(input.shape) { x, y ->
                /**
                 * dy/input + dy/min(x) + dy/max(x)
                 * dy/dx = dNumerator
                 * dy/min(x) = if(x == min(x)) -dNumerator + dDenominator else 0f
                 * dy/max(x) = if(x == max(x)) -dDenominator else 0f
                 */
                val inputValue = input[x, y]
                when (inputValue) {
                    min.get() -> dDenominator.get()
                    max.get() -> dNumerator[x, y] - dDenominator.get()
                    else -> dNumerator[x, y]
                }
            }
        }
    }
}

fun <T> NetworkBuilder.D2<T>.minMaxNorm(
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
) = addProcess(
    process =
    MinMaxNormD2(
        outputX = inputX,
        outputY = inputY,
        optimizer = optimizer.d2(inputX, inputY),
        weight = initializer.d2(
            input = listOf(inputX, inputY),
            output = listOf(inputX, inputY),
            x = inputX,
            y = inputY,
        ),
    ),
)
