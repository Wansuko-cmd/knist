package com.wsr.knist.network.process.compute.padding

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
class PaddingD2 internal constructor(
    private val axis: Int,
    private val left: Int,
    private val right: Int,
    override val inputI: Int,
    override val inputJ: Int,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val outputI: Int = if (axis == 0) inputI + left + right else inputI
    override val outputJ: Int = if (axis == 1) inputJ + left + right else inputJ

    init {
        check(axis == 0 || axis == 1) {
            """
            invalid parameter.
            axis: $axis
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.padding(axis = axis, left = left, right = right)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input.padding(axis = axis, left = left, right = right)
        val delta = calcDelta(output)
        return delta.slice(indices = left until left + if (axis == 0) inputI else inputJ, axis = axis)
    }
}

fun GraphBuilder.Node.D2.padding(axis: Int, left: Int = 0, right: Int = 0, id: String = Uuid.random().toString()) = addCompute(
    compute = PaddingD2(
        axis = axis,
        left = left,
        right = right,
        inputI = inputI,
        inputJ = inputJ,
        id = id,
    ),
)
