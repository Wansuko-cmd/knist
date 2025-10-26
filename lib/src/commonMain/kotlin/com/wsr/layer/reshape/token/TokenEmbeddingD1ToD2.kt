package com.wsr.layer.reshape.token

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.reshape.Reshape
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable
import kotlin.repeat

@Serializable
class TokenEmbeddingD1ToD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val vocabSize: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Reshape.D1ToD2() {

    override fun expect(input: List<IOType.D1>): List<IOType.D2> = forward(input)

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D1> {
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
            dw = dw / input.size.toDouble(),
        )

        // Embedding層は離散的なので、入力への勾配は意味を持たない
        // しかし型の整合性のため、ダミーのD1を返す
        return List(input.size) { IOType.d1(input[it].shape) }
    }

    private fun forward(input: List<IOType.D1>): List<IOType.D2> = input.map { tokenIds ->
        IOType.d2(outputX, outputY) { x, y ->
            val tokenId = tokenIds[x].toInt()
            if (tokenId in 0 until vocabSize) weight[tokenId, y] else 0.0
        }
    }
}

fun <T> NetworkBuilder.D1<T>.tokenEmbedding(
    vocabSize: Int,
    tokenSize: Int,
    optimizer: Optimizer = this.optimizer,
): NetworkBuilder.D2<T> = addReshape(
    reshape = TokenEmbeddingD1ToD2(
        outputX = inputSize,
        outputY = tokenSize,
        vocabSize = vocabSize,
        optimizer = optimizer.d2(vocabSize, tokenSize),
        weight = IOType.d2(vocabSize, tokenSize) { _, _ ->
            random.nextDouble(-0.1, 0.1)
        },
    ),
)
