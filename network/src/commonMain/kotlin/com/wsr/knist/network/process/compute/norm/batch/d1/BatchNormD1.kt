package com.wsr.knist.network.process.compute.norm.batch.d1

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD2
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid

class BatchNormD1(
    override val inputI: Int,
    private val e: Float,
    override val id: String = Uuid.random().toString(),
) : Compute.D1() {
    override val outputI = inputI

    override fun IOScope.expect(
        input: Batch<IOType.D1>,
        env: GraphEnv,
    ): Batch<IOType.D1> {
        TODO("Not yet implemented")
    }

    override fun IOScope.train(
        input: Batch<IOType.D1>,
        env: GraphEnv,
        calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
        val average = input.batchAverage()
        val numerator = input - average

        val variance = numerator.pow(n = 2).batchAverage()
        val denominator = variance.sqrt(e = e)

        val output = numerator / denominator
        val delta = calcDelta(output)

        val dNumerator = delta / denominator

        val dx1 = dNumerator

        val dx2 = -dNumerator.batchAverage()

        // dy/x <- variance(x)のx
        val dx3 = run {
            // dy/d[5]
            val d5 = (delta * -(numerator / denominator.pow(2))).batchAverage()
            val d4 = d5 / (2f * denominator)
            val d3 = d4 * 2f * numerator
            d3 - d3.batchAverage()
        }

        // dy/dx
        return dx1 + dx2 + dx3
    }
}