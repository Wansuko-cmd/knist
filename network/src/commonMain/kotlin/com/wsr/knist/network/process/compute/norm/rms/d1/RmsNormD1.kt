package com.wsr.knist.network.process.compute.norm.rms.d1

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class RmsNormD1 internal constructor(override val inputI: Int, private val e: Float, override val id: String = Uuid.random().toString()) : Compute.D1() {
    override val outputI: Int get() = inputI
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> {
        val deviation = input.pow(n = 2).average().sqrt(e = e)
        return input / deviation
    }

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val variance = input.pow(2).average()
        val deviation = variance.sqrt(e = e)
        val output = input / deviation

        val delta = calcDelta(output)

        val dx1 = delta / deviation
        val dx2 = run {
            val m = (delta * input).average() / deviation.pow(3)
            m * input
        }
        return dx1 - dx2
    }
}

fun GraphBuilder.Node.D1.rmsNorm(e: Float = 1e-6f, id: String = Uuid.random().toString()) = addCompute(
    compute = RmsNormD1(inputI = inputI, e = e, id = id),
)
