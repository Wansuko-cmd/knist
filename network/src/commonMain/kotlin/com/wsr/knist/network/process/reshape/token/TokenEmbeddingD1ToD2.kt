package com.wsr.knist.network.process.reshape.token

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addReshape
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Reshape
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class TokenEmbeddingD1ToD2 internal constructor(
    override val inputI: Int,
    override val outputJ: Int,
    private val vocabSize: Int,
    private var optimizer: Optimizer.D2,
    private var weight: IOType.D2.Global,
    override val id: String = Uuid.random().toString(),
) : Reshape.D1ToD2() {
    override val outputI: Int get() = inputI

    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D2> = input.gather(other = weight)

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D1> {
        val output = input.gather(other = weight)
        val delta = calcDelta(output)

        val dw = delta.scatterAdd(other = input, n = vocabSize)
        weight = optimizer.adapt(weight = weight, dw = dw / input.size.toFloat()).toGlobal()

        // Embedding層は離散的なので、入力への勾配は意味を持たない
        // しかし型の整合性のため、ダミーのD1を返す
        return Batch.d1(input.size, input.shape)
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d2(i = vocabSize, j = outputJ)
    }
}

fun GraphBuilder.Node.D1.tokenEmbedding(
    vocabSize: Int,
    tokenSize: Int,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
    id: String = Uuid.random().toString(),
): GraphBuilder.Node.D2 = addReshape(
    reshape = TokenEmbeddingD1ToD2(
        inputI = inputI,
        outputJ = tokenSize,
        vocabSize = vocabSize,
        optimizer = optimizer.d2(vocabSize, tokenSize),
        weight = initializer.d2(
            input = listOf(vocabSize),
            output = listOf(tokenSize),
            i = vocabSize,
            j = tokenSize,
        ),
        id = id,
    ),
)
