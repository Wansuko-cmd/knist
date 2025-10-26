@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.reshape.token

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
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
            (tokenId + 1).toDouble()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(0.1).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 2, 1, 3]
        val input = listOf(
            IOType.d1(doubleArrayOf(1.0, 2.0, 1.0, 3.0)),
        )

        val result = embedding._expect(input)

        // 検証
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // トークン1の埋め込み: [2, 2, 2]
        assertEquals(expected = 2.0, actual = output[0, 0])
        assertEquals(expected = 2.0, actual = output[0, 1])
        assertEquals(expected = 2.0, actual = output[0, 2])

        // トークン2の埋め込み: [3, 3, 3]
        assertEquals(expected = 3.0, actual = output[1, 0])
        assertEquals(expected = 3.0, actual = output[1, 1])
        assertEquals(expected = 3.0, actual = output[1, 2])

        // トークン1の埋め込み: [2, 2, 2]
        assertEquals(expected = 2.0, actual = output[2, 0])
        assertEquals(expected = 2.0, actual = output[2, 1])
        assertEquals(expected = 2.0, actual = output[2, 2])

        // トークン3の埋め込み: [4, 4, 4]
        assertEquals(expected = 4.0, actual = output[3, 0])
        assertEquals(expected = 4.0, actual = output[3, 1])
        assertEquals(expected = 4.0, actual = output[3, 2])
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_expect=範囲外のトークンIDはゼロベクトル`() {
        val vocabSize = 5
        val embeddingDim = 3
        val seqLen = 3

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toDouble()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(0.1).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 10, 2] (10は範囲外)
        val input = listOf(
            IOType.d1(doubleArrayOf(1.0, 10.0, 2.0)),
        )

        val result = embedding._expect(input)

        val output = result[0] as IOType.D2

        // トークン1: [2, 2, 2]
        assertEquals(expected = 2.0, actual = output[0, 0])

        // トークン10 (範囲外): [0, 0, 0]
        assertEquals(expected = 0.0, actual = output[1, 0])
        assertEquals(expected = 0.0, actual = output[1, 1])
        assertEquals(expected = 0.0, actual = output[1, 2])

        // トークン2: [3, 3, 3]
        assertEquals(expected = 3.0, actual = output[2, 0])
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=勾配が正しく伝播して重みが更新される`() {
        val vocabSize = 3
        val embeddingDim = 2
        val seqLen = 2
        val learningRate = 0.1

        // 初期重み:
        // token 0: [1, 1]
        // token 1: [2, 2]
        // token 2: [3, 3]
        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toDouble()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 2]
        val input = listOf(
            IOType.d1(doubleArrayOf(1.0, 2.0)),
        )

        // 勾配を返すラムダ:
        // 位置0 (token 1): [0.5, 0.5]
        // 位置1 (token 2): [1.0, 1.0]
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.d2(seqLen, embeddingDim) { x, y ->
                    if (x == 0) 0.5 else 1.0
                },
            )
        }

        // 学習前の重みを取得
        val weightBefore = embedding._expect(input)[0] as IOType.D2

        // 学習実行
        embedding._train(input, calcDelta)

        // 学習後の重みを取得
        val weightAfter = embedding._expect(input)[0] as IOType.D2

        // Token 1の重み変化を検証
        // 初期: [2, 2], 勾配: [0.5, 0.5], 更新後: [2 - 0.1*0.5, 2 - 0.1*0.5] = [1.95, 1.95]
        assertTrue(weightBefore[0, 0] > weightAfter[0, 0], "Token 1の重みが減少するべき")
        assertEquals(expected = 1.95, actual = weightAfter[0, 0], absoluteTolerance = 0.01)
        assertEquals(expected = 1.95, actual = weightAfter[0, 1], absoluteTolerance = 0.01)

        // Token 2の重み変化を検証
        // 初期: [3, 3], 勾配: [1.0, 1.0], 更新後: [3 - 0.1*1.0, 3 - 0.1*1.0] = [2.9, 2.9]
        assertTrue(weightBefore[1, 0] > weightAfter[1, 0], "Token 2の重みが減少するべき")
        assertEquals(expected = 2.9, actual = weightAfter[1, 0], absoluteTolerance = 0.01)
        assertEquals(expected = 2.9, actual = weightAfter[1, 1], absoluteTolerance = 0.01)
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=同じトークンが複数回使われた場合勾配が蓄積される`() {
        val vocabSize = 3
        val embeddingDim = 2
        val seqLen = 3
        val learningRate = 0.1

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toDouble()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: トークンID列 [1, 1, 2] (token 1が2回)
        val input = listOf(
            IOType.d1(doubleArrayOf(1.0, 1.0, 2.0)),
        )

        // 勾配: 全て1.0
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.d2(seqLen, embeddingDim) { _, _ -> 1.0 },
            )
        }

        val weightBefore = embedding._expect(input)[0] as IOType.D2
        embedding._train(input, calcDelta)
        val weightAfter = embedding._expect(input)[0] as IOType.D2

        // Token 1は2回使われているので、勾配が2倍蓄積される
        // 初期: [2, 2], 勾配合計: [2, 2], 更新後: [2 - 0.1*2, 2 - 0.1*2] = [1.8, 1.8]
        val deltaToken1 = weightBefore[0, 0] - weightAfter[0, 0]
        assertEquals(expected = 0.2, actual = deltaToken1, absoluteTolerance = 0.01)

        // Token 2は1回のみ (位置2に出現)
        // 初期: [3, 3], 勾配合計: [1, 1], 更新後: [3 - 0.1*1, 3 - 0.1*1] = [2.9, 2.9]
        val deltaToken2 = weightBefore[2, 0] - weightAfter[2, 0]
        assertEquals(expected = 0.1, actual = deltaToken2, absoluteTolerance = 0.01)

        // Token 1の変化量はToken 2の2倍
        assertTrue(abs(deltaToken1 - deltaToken2 * 2) < 0.01, "Token 1の変化量はToken 2の2倍であるべき")
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=バッチ処理で勾配が平均化される`() {
        val vocabSize = 2
        val embeddingDim = 2
        val seqLen = 1
        val learningRate = 1.0

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            1.0
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // バッチサイズ=2
        // バッチ0: token [0], 勾配 [2, 2]
        // バッチ1: token [0], 勾配 [4, 4]
        val input = listOf(
            IOType.d1(doubleArrayOf(0.0)),
            IOType.d1(doubleArrayOf(0.0)),
        )

        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.d2(seqLen, embeddingDim) { _, _ -> 2.0 },
                IOType.d2(seqLen, embeddingDim) { _, _ -> 4.0 },
            )
        }

        embedding._train(input, calcDelta)
        val weightAfter = embedding._expect(listOf(IOType.d1(doubleArrayOf(0.0))))[0] as IOType.D2

        // 勾配平均: (2 + 4) / 2 = 3
        // 更新: 1.0 - 1.0 * 3 = -2.0
        assertEquals(expected = -2.0, actual = weightAfter[0, 0], absoluteTolerance = 0.01)
        assertEquals(expected = -2.0, actual = weightAfter[0, 1], absoluteTolerance = 0.01)
    }

    @Test
    fun `TokenEmbeddingD1ToD2の_train=使われていないトークンの重みは更新されない`() {
        val vocabSize = 4
        val embeddingDim = 2
        val seqLen = 2
        val learningRate = 0.1

        val weight = IOType.d2(vocabSize, embeddingDim) { tokenId, embIdx ->
            (tokenId + 1).toDouble()
        }

        val embedding = TokenEmbeddingD1ToD2(
            outputX = seqLen,
            outputY = embeddingDim,
            vocabSize = vocabSize,
            optimizer = Sgd(learningRate).d2(vocabSize, embeddingDim),
            weight = weight,
        )

        // 入力: [0, 1] (token 2, 3は使われない)
        val input = listOf(
            IOType.d1(doubleArrayOf(0.0, 1.0)),
        )

        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(
                IOType.d2(seqLen, embeddingDim) { _, _ -> 1.0 },
            )
        }

        // Token 2の初期重み (初期値: [3, 3])
        val token2BeforeValue = 3.0  // 初期化時の値

        embedding._train(input, calcDelta)

        // Token 2の重みをチェック (シーケンス長=2なので2要素の入力が必要)
        val inputForToken2 = listOf(IOType.d1(doubleArrayOf(2.0, 2.0)))
        val token2After = embedding._expect(inputForToken2)[0] as IOType.D2

        // Token 2は使われていないので変化しない
        assertEquals(expected = token2BeforeValue, actual = token2After[0, 0])
        assertEquals(expected = token2BeforeValue, actual = token2After[0, 1])
    }
}
