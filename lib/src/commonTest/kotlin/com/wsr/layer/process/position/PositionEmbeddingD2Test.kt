@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.position

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionEmbeddingD2Test {
    @Test
    fun `PositionEmbeddingD2の_expect=入力に位置埋め込みを加算`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val positionEmbedding =
            PositionEmbeddingD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1f).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // 入力は[[5, 6], [7, 8]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )

        // 出力 = 入力 + 位置埋め込み
        // [[5, 6], [7, 8]] + [[1, 2], [3, 4]] = [[6, 8], [10, 12]]
        val result = positionEmbedding._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 6.0f, actual = output[0, 0])
        assertEquals(expected = 8.0f, actual = output[0, 1])
        assertEquals(expected = 10.0f, actual = output[1, 0])
        assertEquals(expected = 12.0f, actual = output[1, 1])
    }

    @Test
    fun `PositionEmbeddingD2の_train=deltaをそのまま返し、位置埋め込みを更新`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val positionEmbedding =
            PositionEmbeddingD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1f).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[5, 6], [7, 8]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )

        // deltaは[[0.1f, 0.2f], [0.3f, 0.4f]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> (x * 2 + y + 1) * 0.1f })
        }

        val result = positionEmbedding._train(input, calcDelta)

        // 位置埋め込みの逆伝播は、加算なのでdeltaをそのまま返す
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0] as IOType.D2
        assertEquals(expected = 0.1f, actual = delta[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.2f, actual = delta[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.3f, actual = delta[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.4f, actual = delta[1, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `PositionEmbeddingD2の_train=位置埋め込みが学習で更新される`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val positionEmbedding =
            PositionEmbeddingD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1f).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[5, 6], [7, 8]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        // trainで位置埋め込みを更新
        // weight -= rate * delta.average() = [[1, 2], [3, 4]] - 0.1f * [[2, 4], [6, 8]]
        //                                   = [[1, 2], [3, 4]] - [[0.2f, 0.4f], [0.6f, 0.8f]]
        //                                   = [[0.8f, 1.6f], [2.4f, 3.2f]]
        positionEmbedding._train(input, calcDelta)

        // 更新後のexpect結果
        // output = input + weight = [[5, 6], [7, 8]] + [[0.8f, 1.6f], [2.4f, 3.2f]]
        //                         = [[5.8f, 7.6f], [9.4f, 11.2f]]
        val afterOutput = positionEmbedding._expect(input)[0] as IOType.D2

        assertEquals(expected = 5.8f, actual = afterOutput[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 7.6f, actual = afterOutput[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 9.4f, actual = afterOutput[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 11.2f, actual = afterOutput[1, 1], absoluteTolerance = 1e-6f)
    }
}
