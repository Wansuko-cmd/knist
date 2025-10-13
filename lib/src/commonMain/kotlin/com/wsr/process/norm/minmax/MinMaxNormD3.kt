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
class MinMaxNormD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }
        return List(input.size) {
            val denominator = max[it] - min[it]
            IOType.d3(
                i = outputX,
                j = outputY,
                k = outputZ,
            ) { x, y, z -> weight[x, y, z] * (input[it][x, y, z] - min[it]) / denominator }
        }
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val min = input.map { it.value.min() }
        val max = input.map { it.value.max() }

        val numerator = List(input.size) {
            IOType.d3(input[it].shape) { x, y, z -> input[it][x, y, z] - min[it] }
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
                IOType.d3(weight.shape) { x, y, z -> mean[x, y, z] * delta[x, y, z] }
            },
        )

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator: List<Double> =
            List(input.size) { -1 * denominator[it].pow(2) * (numerator[it] * dOutput[it]).sum() }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        // 各要素(dy/dx, dy/min(x), dy/max(x))
        val dx1 = List(input.size) { -1.0 * min[it] * dNumerator[it] }
        val dx2 = List(input.size) {
            // dy/min(x) <- max(x) - min(x)側
            val dMin = max[it] * dDenominator[it]
            IOType.d3(outputX, outputY, outputZ) { x, y, z ->
                if (input[it][x, y, z] == min[it]) input[it][x, y, z] * dNumerator[it][x, y, z] + dMin else 0.0
            }
        }
        val dx3 = List(input.size) {
            // dy/max(x)
            val dMax = -1.0 * min[it] * dDenominator[it]
            IOType.d3(outputX, outputY, outputZ) { x, y, z ->
                if (input[it][x, y, z] == max[it]) dMax else 0.0
            }
        }

        return List(input.size) { dx1[it] + dx2[it] + dx3[it] }
    }

    private operator fun IOType.D3.times(other: IOType.D3) = IOType.d3(shape) { x, y, z ->
        this[x, y, z] * other[x, y, z]
    }
}

fun <T : IOType> NetworkBuilder.D3<T>.minMaxNorm(optimizer: Optimizer = this.optimizer) = addProcess(
    process =
    MinMaxNormD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        optimizer = optimizer.d3(inputX, inputY, inputZ),
        weight = IOType.d3(inputX, inputY, inputZ) { _, _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
