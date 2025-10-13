package com.wsr.process.norm

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.pow

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
            dw = IOType.d2(weight.shape) { x, y ->
                (0 until input.size).sumOf { mean[it][x, y] * delta[it][x, y] } / input.size
            },
        )

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator = List(input.size) {
            -1 * denominator[it].pow(2) * (numerator[it] * dOutput[it]).sum()
        }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        // 各要素(dy/dx, dy/min(x), dy/max(x))
        val dx1 = List(input.size) { -1.0 * min[it] * dNumerator[it] }
        val dx2 = List(input.size) {
            // dy/min(x) <- max(x) - min(x)側
            val dMin = max[it] * dDenominator[it]
            IOType.d2(outputX, outputY) { x, y ->
                if (input[it][x, y] == min[it]) input[it][x, y] * dNumerator[it][x, y] + dMin else 0.0
            }
        }
        val dx3 = List(input.size) {
            // dy/max(x)
            val dMax = -1.0 * min[it] * dDenominator[it]
            IOType.d2(outputX, outputY) { x, y ->
                if (input[it][x, y] == max[it]) dMax else 0.0
            }
        }

        return List(input.size) { dx1[it] + dx2[it] + dx3[it] }
    }

    private operator fun IOType.D2.times(other: IOType.D2) = IOType.d2(shape) { x, y ->
        this[x, y] * other[x, y]
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.minMaxNorm(optimizer: Optimizer = this.optimizer) = addProcess(
    process =
        MinMaxNormD2(
            outputX = inputX,
            outputY = inputY,
            optimizer = optimizer.d2(inputX, inputY),
            weight = IOType.d2(inputX, inputY) { _, _ -> random.nextDouble(-1.0, 1.0) },
        ),
)
