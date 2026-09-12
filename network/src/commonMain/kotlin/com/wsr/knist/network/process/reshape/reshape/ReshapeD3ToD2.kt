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
internal class ReshapeD3ToD2(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    override val outputI: Int,
    override val outputJ: Int,
    override val id: String = Uuid.random().toString(),
) : Reshape.D3ToD2() {
    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D2> = input.reshapeToD2(i = outputI, j = outputJ)

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D3> {
        val output = input.reshapeToD2(i = outputI, j = outputJ)
        val delta = calcDelta(output)
        return delta.reshapeToD3(input.shape)
    }
}

fun GraphBuilder.Node.D3.reshapeToD2(i: Int, j: Int, id: String = Uuid.random().toString()): GraphBuilder.Node.D2 {
    check(inputI * inputJ * inputK == i * j) {
        """
            invalid parameter.
            inputI: $inputI
            inputJ: $inputJ
            inputK: $inputK
            outputI: $i
            outputJ: $j
        """.trimIndent()
    }
    return addReshape(
        reshape = ReshapeD3ToD2(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            outputI = i,
            outputJ = j,
            id = id,
        ),
    )
}
