package com.wsr.process.compute.norm.layer.d3

import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
import com.wsr.batch.collecction.sum.sum
import com.wsr.batch.math.pow
import com.wsr.batch.math.sqrt
import com.wsr.batch.operation.div.div
import com.wsr.batch.operation.minus.minus
import com.wsr.batch.operation.plus.plus
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxisD3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val axis: Int,
    private val e: Float,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Compute.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis = axis)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val normalize = numerator.div(other = denominator, axis = axis)
        return weight * normalize
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis = axis)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val normalize = numerator.div(other = denominator, axis = axis)

        val output = weight * normalize
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = normalize * delta,
        )

        // dOutput
        val dOutput = delta * weight

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = dOutput.div(other = denominator, axis = axis)

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- x-average(x)のaverage(x)のx
        val dx2 = -1f * dNumerator.average(axis = axis)

        // dy/x <- variance(x)のx
        val dx3 = run {
            // 各行ごとの勾配を事前計算
            val dvn = (dOutput * normalize).sum(axis = axis)
            val dvd = -2f * outputY.toFloat() * denominator.pow(2)
            val dVariancePerRow = dvn / dvd

            // dy/[x-average(x)]のx部分
            val dSquared = 2f * dVariancePerRow.times(other = numerator, axis = axis)

            // dy/[x-average(x)]のaverage(x)のx部分
            val avgGradient = -2f * dVariancePerRow * numerator.average(axis = axis)

            dSquared.plus(other = avgGradient, axis = axis)
        }

        // dy/dx
        return dx1.plus(dx2, axis = axis) + dx3
    }
}
