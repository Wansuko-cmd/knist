package com.wsr.knist.network.join.mul

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope
import com.wsr.knist.network.join.Join
import kotlinx.serialization.Serializable

@Serializable
internal class MulD3(override val outputI: Int, override val outputJ: Int, override val outputK: Int) : Join.D3() {
    override fun IOScope.expect(inputs: List<Batch<IOType.D3>>, env: GraphEnv): Batch<IOType.D3> = inputs.reduce { acc, batch -> acc * batch }

    override fun IOScope.train(inputs: List<Batch<IOType.D3>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): List<Batch<IOType.D3>> {
        val output = inputs.reduce { acc, batch -> acc * batch }
        val delta = calcDelta(output)
        return List(inputs.size) { index ->
            delta * inputs.toMutableList()
                .apply { removeAt(index) }
                .reduce { acc, batch -> acc * batch }
        }
    }
}

fun GraphScope.mul(vararg nodes: GraphBuilder.Node.D3): GraphBuilder.Node.D3 {
    check(nodes.distinctBy { Triple(it.inputI, it.inputJ, it.inputK) }.size == 1)
    return addJoin(
        join = MulD3(outputI = nodes[0].inputI, outputJ = nodes[0].inputJ, outputK = nodes[0].inputK),
        nodes = nodes,
    )
}
