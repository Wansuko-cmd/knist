package com.wsr.knist.network.process.compute.norm.layer.d2

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxisD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val axis: Int, private val e: Float, override val id: String = Uuid.random().toString()) :
    Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    private val outputT = when (axis) {
        0 -> outputI
        1 -> outputJ
        else -> throw IllegalArgumentException("LayerNormAxisD2 axis is $axis, not 0 or 1.")
    }

    // 四則演算用
    private val basicOpAxis = if (axis == 0) 1 else 0

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis = basicOpAxis)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        return numerator.div(other = denominator, axis = basicOpAxis)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val average = input.average(axis = axis)
        val numerator = input.minus(other = average, axis = basicOpAxis)

        val variance = numerator.pow(2).average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val output = numerator.div(other = denominator, axis = basicOpAxis)

        val delta = calcDelta(output)

        // dy/[x-average(x)] (分子に関する勾配)
        val dNumerator = delta.div(other = denominator, axis = basicOpAxis)

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
            val dSquared = 2f * dVariancePerRow.times(other = numerator, axis = basicOpAxis)

            // dy/[x-average(x)]のaverage(x)のx部分
            val avgGradient = -2f * dVariancePerRow * numerator.average(axis = axis)

            dSquared.plus(other = avgGradient, axis = basicOpAxis)
        }

        // dy/dx
        return dx1.plus(dx2, axis = basicOpAxis) + dx3
    }
}
