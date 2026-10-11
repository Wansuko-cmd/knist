package com.wsr.knist.network.process.compute.norm.batch.d3

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class BatchNormAxisD3(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    private val axis: Int,
    private val e: Float,
    private val momentum: Float,
    override val id: String = Uuid.random().toString(),
) : Compute.D3() {
    override val outputI = inputI
    override val outputJ = inputJ
    override val outputK = inputK

    private val outputR = when (axis) {
        0 -> outputJ to outputK
        1 -> outputI to outputK
        2 -> outputI to outputJ
        else -> throw IllegalArgumentException("BatchNormAxisD3 axis is $axis, not 0, 1 or 2.")
    }

    private val basicOpAxis1 = if (axis == 0) 1 else 0
    private val basicOpAxis2 = if (axis == 2) 1 else 2

    private var rm: IOType.D2.Global = IOType.d2(i = outputR.first, j = outputR.second)
    private var rv: IOType.D2.Global = IOType.d2(i = outputR.first, j = outputR.second) { _, _ -> 1f }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val numerator = input.minus(rm, axis1 = basicOpAxis1, axis2 = basicOpAxis2)
        return numerator.div(rv.sqrt(e = e), axis1 = basicOpAxis1, axis2 = basicOpAxis2)
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val average = input.batchAverage().average(axis = axis)
        val numerator = input.minus(average, axis1 = basicOpAxis1, axis2 = basicOpAxis2)

        val variance = numerator.pow(n = 2).batchAverage().average(axis = axis)
        val denominator = variance.sqrt(e = e)

        val output = numerator.div(denominator, axis1 = basicOpAxis1, axis2 = basicOpAxis2)
        val delta = calcDelta(output)

        val dNumerator = delta.div(denominator, axis1 = basicOpAxis1, axis2 = basicOpAxis2)

        val dx1 = dNumerator
        val dx2 = -dNumerator.batchAverage().average(axis = axis)
        val dx3 = (-output * dNumerator).batchAverage().average(axis = axis)
            .let { output.times(it, axis1 = basicOpAxis1, axis2 = basicOpAxis2) }

        rm = (momentum * rm + (1 - momentum) * average).toGlobal()
        rv = (momentum * rv + (1 - momentum) * variance).toGlobal()

        return dx1.plus(dx2, axis1 = basicOpAxis1, axis2 = basicOpAxis2) + dx3
    }
}
