package com.wsr.knist.network.process.compute.norm.rms.d3

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
class RmsNormD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, private val e: Float, override val id: String = Uuid.random().toString()) :
    Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
        val deviation = input.pow(n = 2).average().sqrt(e = e)
        return input / deviation
    }

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
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

fun GraphBuilder.Node.D3.rmsNorm(axis: Int? = null, e: Float = 1e-6f, id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    val process = when (axis) {
        null -> RmsNormD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            e = e,
            id = id,
        )

        0, 1 -> RmsNormAxisD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            axis = axis,
            e = e,
            id = id,
        )

        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addCompute(compute = process)
}
