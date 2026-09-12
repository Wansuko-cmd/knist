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
internal class GlobalAverageD3ToD2(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Reshape.D3ToD2() {
    override val outputI: Int = inputJ
    override val outputJ: Int = inputK

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D2> = forward(input)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D3> {
        val output = forward(input)
        val delta = calcDelta(output)
        return (delta / inputI.toFloat()).broadcastToD3(axis = 0, size = inputI)
    }

    private fun IOScope.forward(input: Batch<IOType.D3>) = input
        .reshapeToD2(i = inputI, j = inputJ * inputK)
        .average(axis = 0)
        .reshapeToD2(i = inputJ, j = inputK)
}

fun GraphBuilder.Node.D3.globalAverageToD2(id: String = Uuid.random().toString()) = addReshape(
    reshape = GlobalAverageD3ToD2(inputI, inputJ, inputK, id),
)
