package com.wsr.knist.network.process.compute.attention

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.shape.reshapeToD2
import com.wsr.knist.batch.shape.reshapeToD3
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.Graph
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import com.wsr.knist.network.process.compute.attention.bias.AttentionBiasD2
import com.wsr.knist.network.process.compute.attention.bias.AttentionBiasD2Builder
import com.wsr.knist.network.process.compute.attention.bias.backward
import com.wsr.knist.network.process.compute.attention.bias.forward
import kotlin.math.sqrt
import kotlin.uuid.Uuid
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
class AttentionD2 internal constructor(
    override val inputI: Int,
    override val inputJ: Int,
    private val numOfHeads: Int,
    private val dim: Int,
    private val biases: List<@Polymorphic AttentionBiasD2>,
    private var weightQ: IOType.D2.Global,
    private var weightK: IOType.D2.Global,
    private var weightV: IOType.D2.Global,
    private var optimizerQ: Optimizer.D2,
    private var optimizerK: Optimizer.D2,
    private var optimizerV: Optimizer.D2,
    private var weightO: IOType.D2.Global,
    private var optimizerO: Optimizer.D2,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> {
        val query = input.matMul(weightQ)
            .reshapeToD3(i = outputI, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK)
            .reshapeToD3(i = outputI, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV)
            .reshapeToD3(i = outputI, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val mul = query.matMul(key)
        val scaled = mul / sqrt(dim.toFloat())
        val masked = biases.forward(scaled, env)
        val softmax = masked.softmax(axis = 2)
        val heads = softmax.matMul(value)
        val concat = heads
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = outputI, j = numOfHeads * dim)
        return concat.matMul(weightO)
    }

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val query = input.matMul(weightQ)
            .reshapeToD3(i = outputI, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val key = input.matMul(weightK)
            .reshapeToD3(i = outputI, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 2, axisK = 0)

        val value = input.matMul(weightV)
            .reshapeToD3(i = outputI, j = numOfHeads, k = dim)
            .transpose(axisI = 1, axisJ = 0, axisK = 2)

        val mul = query.matMul(key)
        val scaled = mul / sqrt(dim.toFloat())
        val masked = biases.forward(scaled, env)
        val softmax = masked.softmax(axis = 2)
        val heads = softmax.matMul(value)
        val concat = heads
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = outputI, j = numOfHeads * dim)

        val output = concat.matMul(weightO)
        val delta = calcDelta(output)

        // 出力変換（weightO）の逆伝播
        val dConcat = delta.matMul(weightO, transB = true)
        val dwo = concat.matMul(delta, transA = true)
        weightO = optimizerO.adapt(weightO, dwo).toGlobal()

        // Concatの逆伝播（各ヘッドへの勾配に分割）
        val dHeads = dConcat
            .reshapeToD3(outputI, numOfHeads, dim)
            .transpose(1, 0, 2)

        // 各ヘッドのScaled-Dot-Attentionの逆伝播
        val dValue = softmax.matMul(dHeads, transA = true)
        val dSoftmax = dHeads.matMul(value, transB = true)

        val sum = (dSoftmax * softmax).sum(axis = 2)
        val dMasked = softmax * dSoftmax.minus(other = sum, axis1 = 0, axis2 = 1)

        val dScaled = biases.backward(dMasked, env)
        val dMul = dScaled / sqrt(dim.toFloat())

        val dQuery = dMul.matMul(key, transB = true)
        val dKey = query.matMul(dMul, transA = true)

        // Affineの逆伝播（各ヘッドのQ, K, V）
        val dQueryD2 = dQuery
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = outputI, j = numOfHeads * dim)
        val dxq = dQueryD2.matMul(weightQ, transB = true)
        val dwq = input.matMul(dQueryD2, transA = true)

        val dKeyD2 = dKey
            .transpose(axisI = 2, axisJ = 0, axisK = 1)
            .reshapeToD2(i = outputI, j = numOfHeads * dim)
        val dxk = dKeyD2.matMul(weightK, transB = true)
        val dwk = input.matMul(dKeyD2, transA = true)

        val dValueD2 = dValue
            .transpose(axisI = 1, axisJ = 0, axisK = 2)
            .reshapeToD2(i = outputI, j = numOfHeads * dim)
        val dxv = dValueD2.matMul(weightV, transB = true)
        val dwv = input.matMul(dValueD2, transA = true)

        weightQ = optimizerQ.adapt(weightQ, dwq).toGlobal()
        weightK = optimizerK.adapt(weightK, dwk).toGlobal()
        weightV = optimizerV.adapt(weightV, dwv).toGlobal()

        return dxq + dxk + dxv
    }

    override fun update(optimizer: Optimizer) {
        optimizerQ = optimizer.d2(i = inputJ, j = numOfHeads * dim)
        optimizerK = optimizer.d2(i = inputJ, j = numOfHeads * dim)
        optimizerV = optimizer.d2(i = inputJ, j = numOfHeads * dim)
        optimizerO = optimizer.d2(i = numOfHeads * dim, j = inputJ)
    }
}

fun GraphBuilder.Node.D2.attention(
    numOfHeads: Int,
    dim: Int = inputJ / numOfHeads,
    biases: AttentionBiasD2Builder.() -> AttentionBiasD2Builder = { this },
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
    id: String = Uuid.random().toString(),
): GraphBuilder.Node.D2 {
    val biasNodes = mutableListOf<Graph.Node>()
    val compute = AttentionD2(
        inputI = inputI,
        inputJ = inputJ,
        numOfHeads = numOfHeads,
        dim = dim,
        biases = AttentionBiasD2Builder(inputI = inputI, inputJ = inputJ, numOfHeads = numOfHeads)
            .biases()
            .also { biasNodes.addAll(it.nodes) }
            .biases,
        weightQ = initializer.d2(
            input = listOf(inputJ),
            output = listOf(numOfHeads * dim),
            i = inputJ,
            j = numOfHeads * dim,
        ),
        weightK = initializer.d2(
            input = listOf(inputJ),
            output = listOf(numOfHeads * dim),
            i = inputJ,
            j = numOfHeads * dim,
        ),
        weightV = initializer.d2(
            input = listOf(inputJ),
            output = listOf(numOfHeads * dim),
            i = inputJ,
            j = numOfHeads * dim,
        ),
        weightO = initializer.d2(
            input = listOf(numOfHeads * dim),
            output = listOf(inputJ),
            i = numOfHeads * dim,
            j = inputJ,
        ),
        optimizerQ = optimizer.d2(inputJ, numOfHeads * dim),
        optimizerK = optimizer.d2(inputJ, numOfHeads * dim),
        optimizerV = optimizer.d2(inputJ, numOfHeads * dim),
        optimizerO = optimizer.d2(numOfHeads * dim, inputJ),
        id = id,
    )
    val node = Graph.Node.Attach(from = from, process = compute)

    return GraphBuilder.Node.D2(
        from = node.id,
        nodes = nodes + biasNodes + node,
        optimizer = optimizer,
        initializer = initializer,
        inputI = compute.outputI,
        inputJ = compute.outputJ,
    )
}
