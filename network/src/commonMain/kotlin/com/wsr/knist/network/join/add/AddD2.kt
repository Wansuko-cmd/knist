package com.wsr.knist.network.join.add

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope
import com.wsr.knist.network.join.Join
import kotlinx.serialization.Serializable

@Serializable
internal class AddD2(override val outputI: Int, override val outputJ: Int) : Join.D2() {
    override fun IOScope.expect(inputs: List<Batch<IOType.D2>>, env: GraphEnv): Batch<IOType.D2> = inputs.reduce { acc, batch -> acc + batch }

    override fun IOScope.train(inputs: List<Batch<IOType.D2>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): List<Batch<IOType.D2>> {
        val output = inputs.reduce { acc, batch -> acc + batch }
        val delta = calcDelta(output)
        return List(inputs.size) { delta }
    }
}

fun GraphScope.add(vararg nodes: GraphBuilder.Node.D2): GraphBuilder.Node.D2 {
    check(nodes.distinctBy { it.inputI to it.inputJ }.size == 1)
    return addJoin(
        join = AddD2(outputI = nodes[0].inputI, outputJ = nodes[0].inputJ),
        nodes = nodes,
    )
}
