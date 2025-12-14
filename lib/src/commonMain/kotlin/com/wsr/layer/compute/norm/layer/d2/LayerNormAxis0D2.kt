package com.wsr.layer.compute.norm.layer.d2

import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.math.pow
import com.wsr.batch.math.sqrt
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.batch.reshape.broadcast.broadcastToD2
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.compute.Compute
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxis0D2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val e: Float,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Compute.D2() {

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> {
        val average = input.average(axis = 0)
        val numerator = input - average.broadcastToD2(axis = 0, size = outputX)

        val variance = numerator.pow(2).average(axis = 0)
        val denominator = variance.sqrt(e = e)

        val normalize = numerator / denominator.broadcastToD2(axis = 0, size = outputX)
        return weight * normalize
    }

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val average = input.average(axis = 0)
        val numerator = input - average.broadcastToD2(axis = 0, size = outputX)

        val variance = numerator.pow(2).average(axis = 0)
        val denominator = variance.sqrt(e = e)

        val normalize = numerator / denominator.broadcastToD2(axis = 0, size = outputX)

        val output = weight * normalize
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = normalize * delta,
        )

        // dOutput
        val dOutput = delta * weight

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = dOutput / denominator.broadcastToD2(axis = 0, size = outputX)

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- average(x)のx - axis=0なので各列で平均
        val dx2 = -1f * dNumerator.average(axis = 0).broadcastToD2(axis = 0, size = outputX)

        // dy/x <- variance(x)のx
        val dx3 = run {
            // 各列ごとの勾配を事前計算
            val dvn = (dOutput * normalize).sum(axis = 0)
            val dvd = -2f * outputX.toFloat() * denominator.pow(2)
            val dVariancePerCol = dvn / dvd

            // dy/[x-average(x)]のx部分
            val dSquared = 2f * dVariancePerCol.broadcastToD2(axis = 0, size = outputX) * numerator

            // dy/[-average(x)]のx部分 (各列で同じ値なのでbroadcast)
            val avgGradient = -2f * dVariancePerCol * numerator.average(axis = 0)
            val dx2Broadcast = avgGradient.broadcastToD2(axis = 0, size = outputX)

            dSquared + dx2Broadcast
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}
