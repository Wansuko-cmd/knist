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
internal class ReshapeD1ToD3(override val inputI: Int, override val outputI: Int, override val outputJ: Int, override val outputK: Int, override val id: String = Uuid.random().toString()) :
    Reshape.D1ToD3() {
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D3> = input.reshapeToD3(i = outputI, j = outputJ, k = outputK)

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D1> {
        val output = input.reshapeToD3(i = outputI, j = outputJ, k = outputK)
        val delta = calcDelta(output)
        return delta.flatten()
    }
}

fun GraphBuilder.Node.D1.reshapeToD3(i: Int = 1, j: Int = 1, k: Int = inputI, id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    check(i * j * k == inputI) {
        """
            invalid parameter.
            i: $i
            j: $j
            k: $k
        """.trimIndent()
    }
    return addReshape(reshape = ReshapeD1ToD3(inputI = inputI, outputI = i, outputJ = j, outputK = k, id = id))
}
