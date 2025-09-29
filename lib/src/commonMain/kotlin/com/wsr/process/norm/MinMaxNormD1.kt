package com.wsr.process.norm

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.d1.dot
import com.wsr.operation.minus
import com.wsr.operation.plus
import com.wsr.operation.times
import com.wsr.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
class MinMaxNormD1 internal constructor(
    override val outputSize: Int,
    private val rate: Double,
    private var alpha: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }
        return List(input.size) {
            val denominator = max[it] - min[it]
            IOType.d1(outputSize) { x -> alpha[x] * (input[it][x] - min[it]) / denominator }
        }
    }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }

        val numerator = List(input.size) { IOType.d1(input[it].shape) { x -> input[it][x] - min[it] } }
        val denominator = List(numerator.size) { 1 / (max[it] - min[it]) }

        val mean = List(input.size) { denominator[it] * numerator[it] }
        val output = mean.map { alpha * it }

        val delta = calcDelta(output)

        val dOutput = delta.map { it * alpha }

        alpha -= rate * IOType.d1(alpha.shape) { x ->
            (0 until input.size).sumOf { mean[it][x] * delta[it][x] } / input.size
        }

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = List(input.size) { -1 * denominator[it].pow(2) * numerator[it].dot(dOutput[it]) }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        // 各要素(dy/dx, dy/min(x), dy/max(x))
        val dx1 = List(input.size) { -1.0 * min[it] * dNumerator[it] }
        val dx2 = List(input.size) {
            // dy/min(x) <- max(x) - min(x)側
            val dMin = max[it] * dDenominator[it]
            IOType.d1(outputSize) { x ->
                if (input[it][x] == min[it]) input[it][x] * dNumerator[it][x] + dMin else 0.0
            }
        }
        val dx3 = List(input.size) {
            // dy/max(x)
            val dMax = -1.0 * min[it] * dDenominator[it]
            IOType.d1(outputSize) { x ->
                if (input[it][x] == max[it]) dMax else 0.0
            }
        }

        return List(input.size) { dx1[it] + dx2[it] + dx3[it] }
    }

    private operator fun IOType.D1.times(other: IOType.D1) = IOType.d1(shape) { this[it] * other[it] }
}

fun <T : IOType> NetworkBuilder.D1<T>.minMaxNorm() = addProcess(
    process = MinMaxNormD1(
        outputSize = inputSize,
        rate = rate,
        alpha = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)
