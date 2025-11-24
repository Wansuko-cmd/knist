package com.wsr.layer.process.norm.layer.d2

import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.math.pow
import com.wsr.batch.math.sqrt
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.batch.reshape.broadcastToD2
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.optimizer.Optimizer
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
        val numerator = input.minus(other = average, axis = 1)

        val variance = numerator.pow(2).average(axis = 1)
        val denominator = variance.sqrt(e = e)

        val normalize = numerator.div(other = denominator, axis = 1)
        return weight * normalize
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val average = input.average(axis = 1)
        val numerator = input.minus(other = average, axis = 1)

        val variance = numerator.pow(2).average(axis = 1)
        val denominator = variance.sqrt(e = e)

        val normalize = numerator.div(other = denominator, axis = 1)

        val output = weight * normalize
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = normalize * delta,
        )

        // dOutput
        val dOutput = delta * weight

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = dOutput.div(other = denominator, axis = 1)

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx - axis=1なので各行で平均
        val dx2 = -1f * dNumerator.average(axis = 1).broadcastToD2(axis = 1, size = outputY)

        // dy/x <- variance(x)のx
        val dx3 = run {
            // 各行ごとの勾配を事前計算
            val dvn = (dOutput * normalize).sum(axis = 1)
            val dvd = -2f * outputY.toFloat() * denominator.pow(2)
            val dVariancePerRow = dvn / dvd

            // dy/[x-average(x)]のx部分
            val dSquared = 2f * dVariancePerRow.broadcastToD2(axis = 1, size = outputY) * numerator

            // dy/[-average(x)]のx部分 (各行で同じ値なのでbroadcast)
            val avgGradient = -2f * dVariancePerRow * numerator.average(axis = 1)
            val dx2Broadcast = avgGradient.broadcastToD2(axis = 0, size = outputY)

            dSquared + dx2Broadcast
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}
