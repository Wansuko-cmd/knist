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
internal class GlobalAverageD3ToD1(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Reshape.D3ToD1() {
    override val outputI: Int = inputI

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D1> = forward(input = input)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D3> {
        val output = forward(input = input)
        val delta = calcDelta(output)
        return (delta / (inputJ * inputK).toFloat())
            .broadcastToD2(axis = 1, size = inputJ)
            .broadcastToD3(axis = 2, size = inputK)
    }

    private fun IOScope.forward(input: Batch<IOType.D3>) = input
        .reshapeToD2(i = inputI, j = inputJ * inputK)
        .average(axis = 1)
}

fun GraphBuilder.Node.D3.globalAverageToD1(id: String = Uuid.random().toString()) = addReshape(
    reshape = GlobalAverageD3ToD1(inputI, inputJ, inputK, id),
)
