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
) : Compute.D3() {
    private val outputT = when (axis) {
        0 -> outputX
        1 -> outputY
        2 -> outputZ
        else -> throw IllegalArgumentException("LayerNormAxisD3 axis is $axis, not 0, 1 or 2.")
    }
    private val axis1 = when (axis) {
        0 -> 1
        else -> 0
    }
    private val axis2 = when (axis) {
        0, 1 -> 2
        else -> 1
    }

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis1 = axis1, axis2 = axis2)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        return numerator.div(other = denominator, axis1 = axis1, axis2 = axis2)
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis1 = axis1, axis2 = axis2)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val output = numerator.div(other = denominator, axis1 = axis1, axis2 = axis2)
        val delta = calcDelta(output)

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = delta.div(other = denominator, axis1 = axis1, axis2 = axis2)

        // dy/x <- (x-average(x)のx)
        val dx1 = dNumerator

        // dy/x <- x-average(x)のaverage(x)のx
        val dx2 = -1f * dNumerator.average(axis = axis)

        // dy/x <- variance(x)のx
        val dx3 = run {
            // 各行ごとの勾配を事前計算
            val dvn = (delta * output).sum(axis = axis)
            val dvd = -2f * outputT.toFloat() * denominator.pow(2)
            val dVariancePerRow = dvn / dvd

            // dy/[x-average(x)]のx部分
            val dSquared = 2f * dVariancePerRow.times(other = numerator, axis1 = axis1, axis2 = axis2)

            // dy/[x-average(x)]のaverage(x)のx部分
            val avgGradient = -2f * dVariancePerRow * numerator.average(axis = axis)

            dSquared.plus(other = avgGradient, axis1 = axis1, axis2 = axis2)
        }

        // dy/dx
        return dx1.plus(dx2, axis1 = axis1, axis2 = axis2) + dx3
    }
}
