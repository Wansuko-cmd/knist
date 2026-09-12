package com.wsr.knist.network.process.reshape.reshape

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.flatten
import com.wsr.knist.batch.shape.reshapeToD3
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addReshape
import com.wsr.knist.network.process.Reshape
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD1(override val inputI: Int, override val inputJ: Int, override val inputK: Int, override val id: String = Uuid.random().toString()) : Reshape.D3ToD1() {
    override val outputI: Int = inputI * inputJ * inputK

    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D1> = input.flatten()

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D3> {
        val output = input.flatten()
        val delta = calcDelta(output)
        return delta.reshapeToD3(input.shape)
    }
}

fun GraphBuilder.Node.D3.reshapeToD1(id: String = Uuid.random().toString()): GraphBuilder.Node.D1 = addReshape(
    reshape = ReshapeD3ToD1(inputI = inputI, inputJ = inputJ, inputK = inputK, id = id),
)
