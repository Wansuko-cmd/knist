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
internal class GlobalAverageD2ToD1(override val inputI: Int, override val inputJ: Int, override val id: String = Uuid.random().toString()) : Reshape.D2ToD1() {
    override val outputI: Int = inputI

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D1> = input.average(axis = 1)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D2> {
        val output = input.average(axis = 1)
        val delta = calcDelta(output)
        return (delta / inputJ.toFloat()).broadcastToD2(1, inputJ)
    }
}

fun GraphBuilder.Node.D2.globalAverageToD1(id: String = Uuid.random().toString()) = addReshape(
    reshape = GlobalAverageD2ToD1(inputI, inputJ, id),
)
