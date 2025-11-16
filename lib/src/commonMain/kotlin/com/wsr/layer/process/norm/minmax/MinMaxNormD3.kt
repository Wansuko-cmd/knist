package com.wsr.layer.process.norm.minmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.process.Process
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
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
            dw = mean * delta,
        )

        // 分母側(dy/d[max(x) - min(x)])
        val dDenominator: List<Float> =
            List(input.size) { denominator[it].pow(2) * (numerator[it] * dOutput[it]).sum() }

        // 分子側(dy/d[x - min(x)])
        val dNumerator = List(input.size) { denominator[it] * dOutput[it] }

        return List(input.size) {
            IOType.d3(input[it].shape) { x, y, z ->
                /**
                 * dy/input + dy/min(x) + dy/max(x)
                 * dy/dx = dNumerator
                 * dy/min(x) = if(x == min(x)) -dNumerator + dDenominator else 0f
                 * dy/max(x) = if(x == max(x)) -dDenominator else 0f
                 */
                val inputValue = input[it][x, y, z]
                when (inputValue) {
                    min[it] -> dDenominator[it]
                    max[it] -> dNumerator[it][x, y, z] - dDenominator[it]
                    else -> dNumerator[it][x, y, z]
                }
            }
        }
    }

    private operator fun IOType.D3.times(other: IOType.D3) = IOType.d3(shape) { x, y, z ->
        this[x, y, z] * other[x, y, z]
    }
}

fun <T> NetworkBuilder.D3<T>.minMaxNorm(
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
) = addProcess(
    process =
    MinMaxNormD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        optimizer = optimizer.d3(inputX, inputY, inputZ),
        weight = initializer.d3(
            input = listOf(inputX, inputY, inputZ),
            output = listOf(inputX, inputY, inputZ),
            x = inputX,
            y = inputY,
            z = inputZ,
        ),
    ),
)
