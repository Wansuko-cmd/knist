package com.wsr.layer.process.norm.layer.d2

import com.wsr.IOType
import com.wsr.collection.average
import com.wsr.collection.batchAverage
import com.wsr.collection.sum
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import com.wsr.power.pow
import com.wsr.power.sqrt
import com.wsr.reshape.broadcastToD2
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
        val dx2 = -1.0 * dNumerator.average(axis = 1).broadcastToD2(axis = 0, size = outputY)

        // dy/x <- variance(x)のx
        val dx3: List<IOType.D2> = List(input.size) { index ->
            // 各行ごとの勾配を事前計算
            val dVariancePerRow = IOType.d1(outputX) { i ->
                val dvn = -(dOutput[index][i] * normalize[index][i]).sum()
                val dvd = 2.0 * denominator[index][i].pow(2) * outputY.toDouble()
                dvn / dvd
            }

            // dy/[x-average(x)]のx部分
            val dSquared = IOType.d2(outputX, outputY) { i, j ->
                2.0 * dVariancePerRow[i] * numerator[index][i, j]
            }

            // dy/[-average(x)]のx部分 (各行で同じ値なのでbroadcast)
            val avgGradient = -2.0 * dVariancePerRow * numerator[index].average()
            val dx2Broadcast = avgGradient.broadcastToD2(axis = 0, size = outputY)

            dSquared + dx2Broadcast
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}