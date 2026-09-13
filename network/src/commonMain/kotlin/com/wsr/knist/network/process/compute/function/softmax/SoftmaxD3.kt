package com.wsr.knist.network.process.compute.function.softmax

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
class SoftmaxD3 internal constructor(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input.softmax()

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val output = input.softmax()
        val delta = calcDelta(output)
        val sum = (delta * output).sum()
        return output * (delta - sum)
    }
}

fun GraphBuilder.Node.D3.softmax(axis: Int? = null, id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    val process = when (axis) {
        null -> SoftmaxD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            id = id,
        )

        0, 1, 2 -> SoftmaxAxisD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            axis = axis,
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
