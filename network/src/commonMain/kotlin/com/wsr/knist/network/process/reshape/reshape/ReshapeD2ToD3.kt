package com.wsr.knist.network.process.reshape.reshape

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.reshapeToD2
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
internal class ReshapeD2ToD3(
    override val inputI: Int,
    override val inputJ: Int,
    override val outputI: Int,
    override val outputJ: Int,
    override val outputK: Int,
    override val id: String = Uuid.random().toString(),
) : Reshape.D2ToD3() {
    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D3> = input.reshapeToD3(i = outputI, j = outputJ, k = outputK)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D2> {
        val output = input.reshapeToD3(i = outputI, j = outputJ, k = outputK)
        val delta = calcDelta(output)
        return delta.reshapeToD2(input.shape)
    }
}

fun GraphBuilder.Node.D2.reshapeToD3(i: Int = 1, j: Int = inputI, k: Int = inputJ, id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    check(i * j * k == inputI * inputJ) {
        """
            invalid parameter.
            i: $i
            j: $j
            k: $k
        """.trimIndent()
    }
    return addReshape(
        reshape = ReshapeD2ToD3(inputI = inputI, inputJ = inputJ, outputI = i, outputJ = j, outputK = k, id = id),
    )
}
