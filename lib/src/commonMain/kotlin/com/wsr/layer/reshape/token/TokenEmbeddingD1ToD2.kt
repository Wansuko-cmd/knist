package com.wsr.layer.reshape.token

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.d1
import com.wsr.d2
import com.wsr.get
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.set
import kotlin.repeat
import kotlinx.serialization.Serializable

@Serializable
class TokenEmbeddingD1ToD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val vocabSize: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Reshape.D1ToD2() {

    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D1> {
        val input = input
        val output = forward(input)
        val delta = calcDelta(output)

        val dw = IOType.d2(shape = listOf(vocabSize, outputY))
        repeat(input.size) {
            val tokenIds = input[it]
            val gradients = delta[it]

            for (seqIndex in 0 until outputX) {
                val tokenId = tokenIds[seqIndex].toInt()
                if (tokenId in 0 until vocabSize) {
                    dw[tokenId] += gradients[seqIndex]
                }
            }
        }

        weight = optimizer.adapt(
            weight = weight,
            dw = dw / input.size.toFloat(),
        )

        // Embedding層は離散的なので、入力への勾配は意味を持たない
        // しかし型の整合性のため、ダミーのD1を返す
        return Batch(input.size) { IOType.d1(input.shape) }
    }

    private fun forward(input: Batch<IOType.D1>): Batch<IOType.D2> = Batch(input.size) { index ->
        val tokenIds = input[index]
        IOType.d2(outputX, outputY) { x, y ->
            val tokenId = tokenIds[x].toInt()
            if (tokenId in 0 until vocabSize) weight[tokenId, y] else 0f
        }
    }
}

fun <T> NetworkBuilder.D1<T>.tokenEmbedding(
    vocabSize: Int,
    tokenSize: Int,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = this.initializer,
): NetworkBuilder.D2<T> = addReshape(
    reshape = TokenEmbeddingD1ToD2(
        outputX = inputSize,
        outputY = tokenSize,
        vocabSize = vocabSize,
        optimizer = optimizer.d2(vocabSize, tokenSize),
        weight = initializer.d2(
            input = listOf(vocabSize),
            output = listOf(tokenSize),
            x = vocabSize,
            y = tokenSize,
        ),
    ),
)
