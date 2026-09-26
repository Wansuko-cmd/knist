package com.wsr.knist.network.process.reshape.gad

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.reshapeToD2
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addReshape
import com.wsr.knist.network.process.Reshape
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD3ToD2(override val inputI: Int, override val inputJ: Int, override val inputK: Int, private val axis: Int, override val id: String = Uuid.random().toString()) :
    Reshape.D3ToD2() {
    override val outputI: Int = if (axis == 0) inputJ else inputI
    override val outputJ: Int = if (axis == 2) inputJ else inputK

    init {
        check(axis in 0..2) {
            """
            invalid parameter.
            axis: $axis
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D2> = input.average(axis = axis)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D3> {
        val output = input.average(axis = axis)
        val delta = calcDelta(output)
        return (delta / inputShape[axis].toFloat()).broadcastToD3(axis = axis, size = inputShape[axis])
    }
}

fun GraphBuilder.Node.D3.globalAverageToD2(axis: Int, id: String = Uuid.random().toString()) = addReshape(
    reshape = GlobalAverageD3ToD2(inputI, inputJ, inputK, axis, id),
)
