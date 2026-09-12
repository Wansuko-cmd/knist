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
internal class ReshapeD1ToD2(override val inputI: Int, override val outputI: Int, override val outputJ: Int, override val id: String = Uuid.random().toString()) : Reshape.D1ToD2() {
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D2> = input.reshapeToD2(i = outputI, j = outputJ)

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D1> {
        val output = input.reshapeToD2(i = outputI, j = outputJ)
        val delta = calcDelta(output)
        return delta.flatten()
    }
}

fun GraphBuilder.Node.D1.reshapeToD2(i: Int = 1, j: Int = inputI, id: String = Uuid.random().toString()): GraphBuilder.Node.D2 {
    check(i * j == inputI) {
        """
            invalid parameter.
            i: $i
            j: $j
        """.trimIndent()
    }
    return addReshape(reshape = ReshapeD1ToD2(inputI = inputI, outputI = i, outputJ = j, id = id))
}
