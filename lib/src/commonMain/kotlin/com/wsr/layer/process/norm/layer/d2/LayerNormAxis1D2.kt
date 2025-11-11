package com.wsr.layer.process.norm.layer.d2

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.average
import com.wsr.collection.batchAverage
import com.wsr.collection.sum
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.power.pow
import com.wsr.power.sqrt
import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
class LayerNormAxis1D2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        val average = input.average(axis = 1)
        val numerator = input - average

        val variance = numerator.pow(2).average(axis = 1)
        val denominator = variance.map { it.sqrt() }

        val normalize = numerator / denominator
        return weight * normalize
    }

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val average = input.average(axis = 1)
        val numerator = input - average

        val variance = numerator.pow(2).average(axis = 1)
        val denominator = variance.map { it.sqrt() }

        val normalize = numerator / denominator

        val output = weight * normalize
        val delta = calcDelta(output)

        // 重みの更新
        weight = optimizer.adapt(
            weight = weight,
            dw = (normalize * delta).batchAverage(),
        )

        // dOutput
        val dOutput = delta * weight

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = dOutput / denominator

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx - axis=1なので各行で平均
        val dx2: List<IOType.D2> = List(input.size) { index ->
            IOType.d2(outputX, outputY) { i, j ->
                var sum = 0.0
                for (jj in 0 until outputY) {
                    sum += dNumerator[index][i, jj]
                }
                -sum / outputY
            }
        }

        // dy/x <- variance(x)のx
        val dx3: List<IOType.D2> = List(input.size) { index ->
            IOType.d2(outputX, outputY) { i, j ->
                // dy/[sqrt(variance(x))]
                val dvn = -(dOutput[index][i] * normalize[index][i]).sum()
                val dvd = 2.0 * denominator[index][i].pow(2) * outputY.toDouble()
                val dVariance = dvn / dvd

                // dy/[x-average(x)]
                val dSquared = 2.0 * dVariance * numerator[index][i, j]

                // dy/[-average(x)]
                val dx2 = -2.0 * dVariance * numerator[index][i].average()

                dSquared + dx2
            }
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}