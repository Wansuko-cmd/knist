package com.wsr.layer.process.norm.layer.d2

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.average.average
import com.wsr.batch.collection.map
import com.wsr.batch.div.div
import com.wsr.batch.func.pow
import com.wsr.batch.minus.minus
import com.wsr.batch.plus.plus
import com.wsr.batch.reshape.broadcastToD2
import com.wsr.batch.sum.sum
import com.wsr.batch.times.times
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
import com.wsr.power.sqrt
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxis1D2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val e: Float,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val average = input.average(axis = 1)
        val numerator = input - average

        val variance = numerator.pow(2).average(axis = 1)
        val denominator = variance.map { it.sqrt(e) }

        val normalize = numerator / denominator
        return weight * normalize
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val average = input.average(axis = 1)
        val numerator = input - average

        val variance = numerator.pow(2).average(axis = 1)
        val denominator = variance.map { it.sqrt(e) }

        val normalize = numerator / denominator

        val output = weight * normalize
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = normalize * delta,
        )

        // dOutput
        val dOutput = delta * weight

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = dOutput / denominator

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx - axis=1なので各行で平均
        val dx2 = -1f * dNumerator.average(axis = 1).broadcastToD2(axis = 0, size = outputY)

        // dy/x <- variance(x)のx
        val dx3 = run {
            // 各行ごとの勾配を事前計算
            val dvn = (dOutput * normalize).sum(axis = 1)
            val dvd = -2f * outputY.toFloat() * denominator.pow(2)
            val dVariancePerRow = dvn / dvd

            // dy/[x-average(x)]のx部分
            val dSquared = 2f * dVariancePerRow.broadcastToD2(axis = 0, size = outputY) * numerator

            // dy/[-average(x)]のx部分 (各行で同じ値なのでbroadcast)
            val avgGradient = -2f * dVariancePerRow * numerator.average(axis = 1)
            val dx2Broadcast = avgGradient.broadcastToD2(axis = 0, size = outputY)

            dSquared + dx2Broadcast
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}
