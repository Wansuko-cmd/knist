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
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
class LayerNormAxis0D2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> {
        return input.map { data ->
            val average = data.average(axis = 0)
            val numerator = IOType.d2(outputX, outputY) { i, j ->
                data[i, j] - average[j]
            }

            val variance = numerator.pow(2).average(axis = 0)
            val denominator = variance.value.map { sqrt(it + 1e-10) }

            val normalize = IOType.d2(outputX, outputY) { i, j ->
                numerator[i, j] / denominator[j]
            }

            weight * normalize
        }
    }

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val average = input.map { it.average(axis = 0) }
        val numerator = input.mapIndexed { index, data ->
            IOType.d2(outputX, outputY) { i, j ->
                data[i, j] - average[index][j]
            }
        }

        val variance = numerator.map { it.pow(2).average(axis = 0) }
        val denominator = variance.map { it.value.map { v -> sqrt(v + 1e-10) } }

        val normalize = numerator.mapIndexed { index, num ->
            IOType.d2(outputX, outputY) { i, j ->
                num[i, j] / denominator[index][j]
            }
        }

        val output = normalize.map { weight * it }
        val delta = calcDelta(output)

        // 重みの更新
        weight = optimizer.adapt(
            weight = weight,
            dw = (normalize.mapIndexed { index, n -> n * delta[index] }).batchAverage(),
        )

        // dOutput
        val dOutput = delta.map { it * weight }

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = dOutput.mapIndexed { index, dOut ->
            IOType.d2(outputX, outputY) { i, j ->
                dOut[i, j] / denominator[index][j]
            }
        }

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx - axis=0なので各列で平均
        val dx2: List<IOType.D2> = List(input.size) { index ->
            IOType.d2(outputX, outputY) { i, j ->
                var sum = 0.0
                for (ii in 0 until outputX) {
                    sum += dNumerator[index][ii, j]
                }
                -sum / outputX
            }
        }

        // dy/x <- variance(x)のx
        val dx3: List<IOType.D2> = List(input.size) { index ->
            IOType.d2(outputX, outputY) { i, j ->
                // dy/[sqrt(variance(x))]
                var dvn = 0.0
                for (ii in 0 until outputX) {
                    dvn -= dOutput[index][ii, j] * normalize[index][ii, j]
                }

                val dvd = 2.0 * denominator[index][j].pow(2) * outputX.toDouble()
                val dVariance = dvn / dvd

                // dy/[x-average(x)]
                val dSquared = 2.0 * dVariance * numerator[index][i, j]

                // dy/[-average(x)]
                val dx2 = -2.0 * dVariance * numerator[index].average()

                dSquared + dx2
            }
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}