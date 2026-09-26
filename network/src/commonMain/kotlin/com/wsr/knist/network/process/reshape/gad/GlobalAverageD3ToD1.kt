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
internal class GlobalAverageD3ToD1(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    private val axis1: Int,
    private val axis2: Int,
    override val id: String = Uuid.random().toString(),
) : Reshape.D3ToD1() {
    override val outputI: Int = when (axis1 to axis2) {
        0 to 1 -> inputK
        0 to 2 -> inputJ
        else -> inputI
    }

    init {
        check(axis1 in 0..2 && axis2 in 0..2 && axis1 < axis2) {
            """
            invalid parameter.
            axis1: $axis1
            axis2: $axis2
            """.trimIndent()
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D1> = input.average(axis = axis1).average(axis = axis2 - 1)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D3> {
        val output = input.average(axis = axis1).average(axis = axis2 - 1)
        val delta = calcDelta(output)
        return (delta / (inputShape[axis1] * inputShape[axis2]).toFloat())
            .broadcastToD2(axis = axis1, size = inputShape[axis1])
            .broadcastToD3(axis = axis2, size = inputShape[axis2])
    }
}

fun GraphBuilder.Node.D3.globalAverageToD1(axis1: Int, axis2: Int, id: String = Uuid.random().toString()) = addReshape(
    reshape = GlobalAverageD3ToD1(inputI, inputJ, inputK, axis1, axis2, id),
)
