package com.wsr.knist.network.join.concat

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope
import com.wsr.knist.network.join.Join
import kotlinx.serialization.Serializable

@Serializable
internal class ConcatD3(override val outputI: Int, override val outputJ: Int, override val outputK: Int, private val axis: Int) : Join.D3() {
    override fun IOScope.expect(inputs: List<Batch<IOType.D3>>, env: GraphEnv): Batch<IOType.D3> = inputs.reduce { acc, batch -> acc.concat(batch, axis = axis) }

    override fun IOScope.train(inputs: List<Batch<IOType.D3>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): List<Batch<IOType.D3>> {
        val output = inputs.reduce { acc, batch -> acc.concat(batch, axis = axis) }
        val delta = calcDelta(output)
        var from = 0
        return when (axis) {
            0 -> List(inputs.size) {
                val index = inputs[it].i
                delta.slice(from until from + index, axis = 0).also { from += index }
            }

            1 -> List(inputs.size) {
                val index = inputs[it].j
                delta.slice(from until from + index, axis = 1).also { from += index }
            }

            else -> List(inputs.size) {
                val index = inputs[it].k
                delta.slice(from until from + index, axis = 2).also { from += index }
            }
        }
    }
}

fun GraphScope.concat(vararg nodes: GraphBuilder.Node.D3, axis: Int): GraphBuilder.Node.D3 = when (axis) {
    0 -> {
        check(nodes.distinctBy { it.inputJ to it.inputK }.size == 1)
        return addJoin(
            join = ConcatD3(
                outputI = nodes.sumOf { it.inputI },
                outputJ = nodes[0].inputJ,
                outputK = nodes[0].inputK,
                axis = 0,
            ),
            nodes = nodes,
        )
    }

    1 -> {
        check(nodes.distinctBy { it.inputI to it.inputK }.size == 1)
        return addJoin(
            join = ConcatD3(
                outputI = nodes[0].inputI,
                outputJ = nodes.sumOf { it.inputJ },
                outputK = nodes[0].inputK,
                axis = 1,
            ),
            nodes = nodes,
        )
    }

    2 -> {
        check(nodes.distinctBy { it.inputI to it.inputJ }.size == 1)
        return addJoin(
            join = ConcatD3(
                outputI = nodes[0].inputI,
                outputJ = nodes[0].inputJ,
                outputK = nodes.sumOf { it.inputK },
                axis = 2,
            ),
            nodes = nodes,
        )
    }

    else -> error("axis is $axis, not 0, 1 or 2.")
}
