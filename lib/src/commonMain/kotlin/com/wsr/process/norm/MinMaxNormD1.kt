package com.wsr.process.norm

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.dot.dot
import com.wsr.operator.minus
import com.wsr.operator.plus
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
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }
        return List(input.size) {
            val denominator = max[it] - min[it]
            IOType.d1(outputSize) { x -> weight[x] * (input[it][x] - min[it]) / denominator }
        }
    }

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }

        val numerator =
            List(input.size) { IOType.d1(input[it].shape) { x -> input[it][x] - min[it] } }
        val denominator = List(numerator.size) { 1 / (max[it] - min[it]) }

        val mean = List(input.size) { denominator[it] * numerator[it] }
        val output = mean.map { weight * it }

        val delta = calcDelta(output)

        val dOutput = delta.map { it * weight }

        weight -= optimizer.adapt(
            dw = IOType.d1(weight.shape) { x ->
                (0 until input.size).sumOf { mean[it][x] * delta[it][x] } / input.size
            },
        )

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator =
            List(input.size) { -1 * denominator[it].pow(2) * numerator[it].dot(dOutput[it]) }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        // 各要素(dy/dx, dy/min(x), dy/max(x))
        val dx1 = List(input.size) { -1.0 * min[it] * dNumerator[it] }
        val dx2 =
            List(input.size) {
                // dy/min(x) <- max(x) - min(x)側
                val dMin = max[it] * dDenominator[it]
                IOType.d1(outputSize) { x ->
                    if (input[it][x] == min[it]) input[it][x] * dNumerator[it][x] + dMin else 0.0
                }
            }
        val dx3 =
            List(input.size) {
                // dy/max(x)
                val dMax = -1.0 * min[it] * dDenominator[it]
                IOType.d1(outputSize) { x ->
                    if (input[it][x] == max[it]) dMax else 0.0
                }
            }

        return List(input.size) { dx1[it] + dx2[it] + dx3[it] }
    }

    private operator fun IOType.D1.times(other: IOType.D1) = IOType.d1(shape) {
        this[it] * other[it]
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.minMaxNorm(optimizer: Optimizer = this.optimizer) = addProcess(
    process =
    MinMaxNormD1(
        outputSize = inputSize,
        optimizer = optimizer.d1(inputSize),
        weight = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)
