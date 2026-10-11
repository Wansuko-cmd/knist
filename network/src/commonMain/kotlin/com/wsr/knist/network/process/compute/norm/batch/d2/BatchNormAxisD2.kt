package com.wsr.knist.network.process.compute.norm.batch.d2

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class BatchNormAxisD2(
    override val inputI: Int,
    override val inputJ: Int,
    private val axis: Int,
    private val e: Float,
    private val momentum: Float,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val outputI = inputI
    override val outputJ = inputJ
    private val outputR = when (axis) {
        0 -> outputJ
        1 -> outputI
        else -> throw IllegalArgumentException("BatchNormAxisD2 axis is $axis, not 0 or 1.")
    }

    private val basicOpAxis = if (axis == 0) 1 else 0

    private var rm: IOType.D1.Global = IOType.d1(i = outputR)
    private var rv: IOType.D1.Global = IOType.d1(i = outputR) { 1f }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val numerator = input.minus(rm, axis = basicOpAxis)
        return numerator.div(rv.sqrt(e = e), axis = basicOpAxis)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val average = input.batchAverage().average(axis = axis)
        val numerator = input.minus(average, axis = basicOpAxis)

        val variance = numerator.pow(n = 2).batchAverage().average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val output = numerator.div(denominator, axis = basicOpAxis)
        val delta = calcDelta(output)

        val dNumerator = delta.div(denominator, axis = basicOpAxis)

        val dx1 = dNumerator
        val dx2 = -dNumerator.batchAverage().average(axis = axis)
        val dx3 = (-output * dNumerator).batchAverage().average(axis = axis)
            .let { output.times(it, axis = basicOpAxis) }

        rm = (momentum * rm + (1 - momentum) * average).toGlobal()
        rv = (momentum * rv + (1 - momentum) * variance).toGlobal()

        return dx1.plus(dx2, axis = basicOpAxis) + dx3
    }
}
