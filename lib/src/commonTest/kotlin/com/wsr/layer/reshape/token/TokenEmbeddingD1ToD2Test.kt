@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.reshape.token

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batchOf
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.optimizer.sgd.Sgd
import com.wsr.core.set
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenEmbeddingD1ToD2Test {
    @Test
    fun `TokenEmbeddingD1ToD2の_expect=トークンIDから埋め込みベクトルを取得`() {
        // 語彙サイズ=5, 埋め込み次元=3, シーケンス長=4
        val vocabSize = 5
        val embeddingDim = 3
        val seqLen = 4

        // 埋め込み重み: 各トークンに固定の埋め込みを設定
        // token 0: [1, 1, 1]
        // token 1: [2, 2, 2]
        // token 2: [3, 3, 3]
        // token 3: [4, 4, 4]
        // token 4: [5, 5, 5]
        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toFloat()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(0.1f).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 2, 1, 3]
        val input = batchOf(
            IOType.d1(floatArrayOf(1.0f, 2.0f, 1.0f, 3.0f)),
        )
        val context = Context(input)

        val result = embedding._expect(input, context) as Batch<IOType.D2>
        // 検証
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]

        // トークン1の埋め込み: [2, 2, 2]
        assertEquals(expected = 2.0f, actual = output[0, 0])
        assertEquals(expected = 2.0f, actual = output[0, 1])
        assertEquals(expected = 2.0f, actual = output[0, 2])

        // トークン2の埋め込み: [3, 3, 3]
        assertEquals(expected = 3.0f, actual = output[1, 0])
        assertEquals(expected = 3.0f, actual = output[1, 1])
        assertEquals(expected = 3.0f, actual = output[1, 2])

        // トークン1の埋め込み: [2, 2, 2]
        assertEquals(expected = 2.0f, actual = output[2, 0])
        assertEquals(expected = 2.0f, actual = output[2, 1])
        assertEquals(expected = 2.0f, actual = output[2, 2])

        // トークン3の埋め込み: [4, 4, 4]
        assertEquals(expected = 4.0f, actual = output[3, 0])
        assertEquals(expected = 4.0f, actual = output[3, 1])
        assertEquals(expected = 4.0f, actual = output[3, 2])
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_expect=範囲外のトークンIDはゼロベクトル`() {
        val vocabSize = 5
        val embeddingDim = 3
        val seqLen = 3

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toFloat()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(0.1f).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 10, 2] (10は範囲外)
        val input = batchOf(
            IOType.d1(floatArrayOf(1.0f, 10.0f, 2.0f)),
        )
        val context = Context(input)

        val result = embedding._expect(input, context) as Batch<IOType.D2>
        val output = result[0]

        // トークン1: [2, 2, 2]
        assertEquals(expected = 2.0f, actual = output[0, 0])

        // トークン10 (範囲外): [0, 0, 0]
        assertEquals(expected = 0.0f, actual = output[1, 0])
        assertEquals(expected = 0.0f, actual = output[1, 1])
        assertEquals(expected = 0.0f, actual = output[1, 2])

        // トークン2: [3, 3, 3]
        assertEquals(expected = 3.0f, actual = output[2, 0])
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=勾配が正しく伝播して重みが更新される`() {
        val vocabSize = 3
        val embeddingDim = 2
        val seqLen = 2
        val learningRate = 0.1f

        // 初期重み:
        // token 0: [1, 1]
        // token 1: [2, 2]
        // token 2: [3, 3]
        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toFloat()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 2]
        val input = batchOf(
            IOType.d1(floatArrayOf(1.0f, 2.0f)),
        )
        val context = Context(input)

        // 勾配を返すラムダ:
        // 位置0 (token 1): [0.5f, 0.5f]
        // 位置1 (token 2): [1.0f, 1.0f]
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d2(seqLen, embeddingDim) { x, y ->
                    if (x == 0) 0.5f else 1.0f
                },
            )
        }

        // 学習前の重みを取得
        val weightBefore = embedding._expect(input, context) as Batch<IOType.D2>

        // 学習実行
        embedding._train(input, context, calcDelta) as Batch<IOType.D1>
        // 学習後の重みを取得
        val weightAfter = embedding._expect(input, context) as Batch<IOType.D2>

        // Token 1の重み変化を検証
        // 初期: [2, 2], 勾配: [0.5f, 0.5f], 更新後: [2 - 0.1f*0.5f, 2 - 0.1f*0.5f] = [1.95f, 1.95f]
        assertTrue(weightBefore[0][0, 0] > weightAfter[0][0, 0], "Token 1の重みが減少するべき")
        assertEquals(expected = 1.95f, actual = weightAfter[0][0, 0], absoluteTolerance = 0.01f)
        assertEquals(expected = 1.95f, actual = weightAfter[0][0, 1], absoluteTolerance = 0.01f)

        // Token 2の重み変化を検証
        // 初期: [3, 3], 勾配: [1.0f, 1.0f], 更新後: [3 - 0.1f*1.0f, 3 - 0.1f*1.0f] = [2.9f, 2.9f]
        assertTrue(weightBefore[0][1, 0] > weightAfter[0][1, 0], "Token 2の重みが減少するべき")
        assertEquals(expected = 2.9f, actual = weightAfter[0][1, 0], absoluteTolerance = 0.01f)
        assertEquals(expected = 2.9f, actual = weightAfter[0][1, 1], absoluteTolerance = 0.01f)
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=同じトークンが複数回使われた場合勾配が蓄積される`() {
        val vocabSize = 3
        val embeddingDim = 2
        val seqLen = 3
        val learningRate = 0.1f

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toFloat()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 1, 2] (token 1が2回)
        val input = batchOf(
            IOType.d1(floatArrayOf(1.0f, 1.0f, 2.0f)),
        )
        val context = Context(input)

        // 勾配: 全て1.0
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d2(seqLen, embeddingDim) { _, _ -> 1.0f },
            )
        }

        val weightBefore = embedding._expect(input, context) as Batch<IOType.D2>
        embedding._train(input, context, calcDelta) as Batch<IOType.D1>
        val weightAfter = embedding._expect(input, context) as Batch<IOType.D2>

        // Token 1は2回使われているので、勾配が2倍蓄積される
        // 初期: [2, 2], 勾配合計: [2, 2], 更新後: [2 - 0.1f*2, 2 - 0.1f*2] = [1.8f, 1.8f]
        val deltaToken1 = weightBefore[0][0, 0] - weightAfter[0][0, 0]
        assertEquals(expected = 0.2f, actual = deltaToken1, absoluteTolerance = 0.01f)

        // Token 2は1回のみ (位置2に出現)
        // 初期: [3, 3], 勾配合計: [1, 1], 更新後: [3 - 0.1f*1, 3 - 0.1f*1] = [2.9f, 2.9f]
        val deltaToken2 = weightBefore[0][2, 0] - weightAfter[0][2, 0]
        assertEquals(expected = 0.1f, actual = deltaToken2, absoluteTolerance = 0.01f)

        // Token 1の変化量はToken 2の2倍
        assertTrue(abs(deltaToken1 - deltaToken2 * 2) < 0.01f, "Token 1の変化量はToken 2の2倍であるべき")
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=使われていないトークンの重みは更新されない`() {
        val vocabSize = 4
        val embeddingDim = 2
        val seqLen = 2
        val learningRate = 0.1f

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toFloat()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: [0, 1] (token 2, 3は使われない)
        val input = batchOf(
            IOType.d1(floatArrayOf(0.0f, 1.0f)),
        )
        val context = Context(input)

        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(
                IOType.d2(seqLen, embeddingDim) { _, _ -> 1.0f },
            )
        }

        // Token 2の初期重み (初期値: [3, 3])
        val token2BeforeValue = 3.0f // 初期化時の値

        embedding._train(input, context, calcDelta) as Batch<IOType.D1>
        // Token 2の重みをチェック (シーケンス長=2なので2要素の入力が必要)
        val inputForToken2 = batchOf(IOType.d1(floatArrayOf(2.0f, 2.0f)))
        val token2After = embedding._expect(inputForToken2, context) as Batch<IOType.D2>

        // Token 2は使われていないので変化しない
        assertEquals(expected = token2BeforeValue, actual = token2After[0][0, 0])
        assertEquals(expected = token2BeforeValue, actual = token2After[0][0, 1])
    }
}
