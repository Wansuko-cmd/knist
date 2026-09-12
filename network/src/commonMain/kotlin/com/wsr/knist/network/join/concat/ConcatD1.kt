package com.wsr.knist.network.join.concat

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.i
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope
import com.wsr.knist.network.join.Join
import kotlinx.serialization.Serializable

@Serializable
internal class ConcatD1(override val outputI: Int) : Join.D1() {
    override fun IOScope.expect(inputs: List<Batch<IOType.D1>>, env: GraphEnv): Batch<IOType.D1> = inputs.reduce { acc, batch -> acc.concat(batch) }

    override fun IOScope.train(inputs: List<Batch<IOType.D1>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): List<Batch<IOType.D1>> {
        val output = inputs.reduce { acc, batch -> acc.concat(batch) }
        val delta = calcDelta(output)
        var from = 0
        return List(inputs.size) {
            val index = inputs[it].i
            delta.slice(from until from + index).also { from += index }
        }
    }
}

fun GraphScope.concat(vararg nodes: GraphBuilder.Node.D1): GraphBuilder.Node.D1 = addJoin(
    join = ConcatD1(outputI = nodes.sumOf { it.inputI }),
    nodes = nodes,
)
