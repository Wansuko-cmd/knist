package com.wsr.knist.network.process.compute.attention.bias

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.network.Graph
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphId
import kotlin.math.abs
import kotlin.math.pow
import kotlinx.serialization.Serializable

context(scope: IOScope)
fun List<AttentionBiasD2>.forward(scaled: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = fold(scaled) { batch, bias ->
    with(bias) { scope.forward(batch, env) }
}

context(scope: IOScope)
fun List<AttentionBiasD2>.backward(delta: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = foldRight(delta) { bias, batch ->
    with(bias) { scope.backward(batch, env) }
}

interface AttentionBiasD2 {
    fun IOScope.forward(scaled: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3>
    fun IOScope.backward(delta: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = delta

    @Serializable
    data class Causal(val inputI: Int) : AttentionBiasD2 {
        private val mask by lazy {
            IOType.d2(inputI, inputI) { x, y -> if (x < y) -1e9f else 0f }
        }

        override fun IOScope.forward(scaled: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = scaled + mask
    }

    @Serializable
    data class Mask(val nodeId: GraphId, val value: Float) : AttentionBiasD2 {
        override fun IOScope.forward(scaled: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> {
            val input: Batch<IOType.D1> = env[nodeId]
            val mask = input.where(onTrue = -1e9f, onFalse = 0f) { it eq value }
            return scaled.plus(other = mask, axis = 2)
        }
    }

    @Serializable
    data class ALiBi(val numOfHeads: Int, val inputI: Int) : AttentionBiasD2 {
        private val bias by lazy {
            IOType.d3(numOfHeads, inputI, inputI) { i, j, k ->
                val slope = 2f.pow(-8f * (i + 1) / numOfHeads)
                -slope * abs(j - k)
            }
        }

        override fun IOScope.forward(scaled: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = scaled + bias
    }
}

data class AttentionBiasD2Builder(val inputI: Int, val inputJ: Int, val numOfHeads: Int, val nodes: List<Graph.Node> = emptyList(), val biases: List<AttentionBiasD2> = emptyList()) {
    fun causal() = copy(biases = biases + AttentionBiasD2.Causal(inputI))

    fun mask(node: GraphBuilder.Node.D1, value: Float): AttentionBiasD2Builder {
        val observer = Graph.Node.Observe(from = node.from)
        return copy(
            nodes = nodes + observer,
            biases = biases + AttentionBiasD2.Mask(nodeId = observer.id, value = value),
        )
    }

    fun alibi() = copy(biases = biases + AttentionBiasD2.ALiBi(numOfHeads, inputI))
}
