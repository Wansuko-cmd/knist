package com.wsr.knist.network.process.compute.norm.rms.d2

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class RmsNormAxisD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val axis: Int, private val e: Float, override val id: String = Uuid.random().toString()) :
    Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ

    // 四則演算用
    private val basicOpAxis = if (axis == 0) 1 else 0

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val deviation = input.pow(n = 2).average(axis = axis).sqrt(e = e)
        return input.div(other = deviation, axis = basicOpAxis)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val variance = input.pow(2).average(axis = axis)
        val deviation = variance.sqrt(e = e)
        val output = input.div(other = deviation, axis = basicOpAxis)

        val delta = calcDelta(output)

        val dx1 = delta.div(other = deviation, axis = basicOpAxis)
        val dx2 = run {
            val m = (delta * input).average(axis = axis) / deviation.pow(3)
            m.times(other = input, axis = basicOpAxis)
        }
        return dx1 - dx2
    }
}
