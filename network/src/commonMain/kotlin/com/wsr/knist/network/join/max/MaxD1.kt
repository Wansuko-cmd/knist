package com.wsr.knist.network.join.max

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope
import com.wsr.knist.network.join.Join
import kotlinx.serialization.Serializable

@Serializable
internal class MaxD1(override val outputI: Int) : Join.D1() {
    override fun IOScope.expect(inputs: List<Batch<IOType.D1>>, env: GraphEnv): Batch<IOType.D1> = inputs.reduce { acc, batch -> where(condition = acc gt batch, onTrue = acc, onFalse = batch) }

    override fun IOScope.train(inputs: List<Batch<IOType.D1>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): List<Batch<IOType.D1>> {
        val output = inputs.reduce { acc, batch -> where(condition = acc gt batch, onTrue = acc, onFalse = batch) }
        val delta = calcDelta(output)
        return inputs.map { input -> where(condition = input eq output, onTrue = delta, onFalse = 0f) }
    }
}

fun GraphScope.max(vararg nodes: GraphBuilder.Node.D1): GraphBuilder.Node.D1 {
    check(nodes.distinctBy { it.inputI }.size == 1)
    return addJoin(
        join = MaxD1(outputI = nodes[0].inputI),
        nodes = nodes,
    )
}
