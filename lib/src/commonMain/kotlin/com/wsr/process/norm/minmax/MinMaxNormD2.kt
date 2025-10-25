package com.wsr.process.norm.minmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.batchAverage
import com.wsr.collection.sum
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.process.Process
import kotlin.math.pow
import kotlinx.serialization.Serializable

@Serializable
class MinMaxNormD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }
        return List(input.size) {
            val denominator = max[it] - min[it]
            IOType.d2(outputX, outputY) { x, y -> weight[x, y] * (input[it][x, y] - min[it]) / denominator }
        }
    }

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }

        val numerator = List(input.size) {
            IOType.d2(input[it].shape) { x, y -> input[it][x, y] - min[it] }
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
                IOType.d2(weight.shape) { x, y -> mean[x, y] * delta[x, y] }
            },
        )

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = List(input.size) {
            denominator[it].pow(2) * (numerator[it] * dOutput[it]).sum()
        }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        return List(input.size) {
            IOType.d2(input[it].shape) { x, y ->
                /**
                 * dy/input + dy/min(x) + dy/max(x)
                 * dy/dx = dNumerator
                 * dy/min(x) = if(x == min(x)) -dNumerator + dDenominator else 0.0
                 * dy/max(x) = if(x == max(x)) -dDenominator else 0.0
                 */
                val inputValue = input[it][x, y]
                when (inputValue) {
                    min[it] -> dDenominator[it]
                    max[it] -> dNumerator[it][x, y] - dDenominator[it]
                    else -> dNumerator[it][x, y]
                }
            }
        }
    }

    private operator fun IOType.D2.times(other: IOType.D2) = IOType.d2(shape) { x, y ->
        this[x, y] * other[x, y]
    }
}

fun <T> NetworkBuilder.D2<T>.minMaxNorm(optimizer: Optimizer = this.optimizer) = addProcess(
    process =
    MinMaxNormD2(
        outputX = inputX,
        outputY = inputY,
        optimizer = optimizer.d2(inputX, inputY),
        weight = IOType.d2(inputX, inputY) { _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
