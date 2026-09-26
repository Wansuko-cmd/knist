package com.wsr.knist.network.process.reshape.gad

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addReshape
import com.wsr.knist.network.process.Reshape
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD2ToD1(override val inputI: Int, override val inputJ: Int, private val axis: Int, override val id: String = Uuid.random().toString()) : Reshape.D2ToD1() {
    override val outputI: Int = if (axis == 0) inputJ else inputI

    init {
        check(axis == 0 || axis == 1) {
            """
            invalid parameter.
            axis: $axis
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D1> = input.average(axis = axis)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D2> {
        val output = input.average(axis = axis)
        val delta = calcDelta(output)
        return (delta / inputShape[axis].toFloat()).broadcastToD2(axis = axis, size = inputShape[axis])
    }
}

fun GraphBuilder.Node.D2.globalAverageToD1(axis: Int, id: String = Uuid.random().toString()) = addReshape(
    reshape = GlobalAverageD2ToD1(inputI, inputJ, axis, id),
)
