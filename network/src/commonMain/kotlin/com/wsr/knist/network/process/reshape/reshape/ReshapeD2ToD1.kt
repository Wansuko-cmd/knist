package com.wsr.knist.network.process.reshape.reshape

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.flatten
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
internal class ReshapeD2ToD1(override val inputI: Int, override val inputJ: Int, override val id: String = Uuid.random().toString()) : Reshape.D2ToD1() {
    override val outputI: Int = inputI * inputJ

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D1> = input.flatten()

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D2> {
        val output = input.flatten()
        val delta = calcDelta(output)
        return delta.reshapeToD2(input.shape)
    }
}

fun GraphBuilder.Node.D2.reshapeToD1(id: String = Uuid.random().toString()) = addReshape(
    reshape = ReshapeD2ToD1(inputI = inputI, inputJ = inputJ, id = id),
)
