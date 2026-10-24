package com.wsr.knist.network.process.compute.norm.layer.d2

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.unaryMinus
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

    init {
        require(axis == 0 || axis == 1) { "LayerNormAxisD2 axis is $axis, not 0 or 1." }
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

        val dNumerator = delta.div(other = denominator, axis = basicOpAxis)

        val dx1 = dNumerator
        val dx2 = -dNumerator.average(axis = axis)
        val dx3 = (-output * dNumerator).average(axis = axis)
            .let { output.times(other = it, axis = basicOpAxis) }

        return dx1.plus(dx2, axis = basicOpAxis) + dx3
    }
}
