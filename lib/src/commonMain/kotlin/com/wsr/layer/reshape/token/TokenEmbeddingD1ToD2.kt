package com.wsr.layer.reshape.token

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.operator.div
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
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

    override fun expect(input: List<IOType.D1>, context: Context): List<IOType.D2> = forward(input)

    override fun train(input: List<IOType.D1>, context: Context, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D1> {
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
        return List(input.size) { IOType.d1(input[it].shape) }
    }

    private fun forward(input: List<IOType.D1>): List<IOType.D2> = input.map { tokenIds ->
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
