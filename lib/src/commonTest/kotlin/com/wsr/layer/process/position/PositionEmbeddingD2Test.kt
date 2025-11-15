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
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val positionEmbedding =
            PositionEmbeddingD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // 入力は[[5, 6], [7, 8]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )

        // 出力 = 入力 + 位置埋め込み
        // [[5, 6], [7, 8]] + [[1, 2], [3, 4]] = [[6, 8], [10, 12]]
        val result = positionEmbedding._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 6.0, actual = output[0, 0])
        assertEquals(expected = 8.0, actual = output[0, 1])
        assertEquals(expected = 10.0, actual = output[1, 0])
        assertEquals(expected = 12.0, actual = output[1, 1])
    }

    @Test
    fun `PositionEmbeddingD2の_train=deltaをそのまま返し、位置埋め込みを更新`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val positionEmbedding =
            PositionEmbeddingD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[5, 6], [7, 8]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )

        // deltaは[[0.1, 0.2], [0.3, 0.4]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> (x * 2 + y + 1) * 0.1 })
        }

        val result = positionEmbedding._train(input, calcDelta)

        // 位置埋め込みの逆伝播は、加算なのでdeltaをそのまま返す
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0] as IOType.D2
        assertEquals(expected = 0.1, actual = delta[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = delta[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = delta[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = delta[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `PositionEmbeddingD2の_train=位置埋め込みが学習で更新される`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val positionEmbedding =
            PositionEmbeddingD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[5, 6], [7, 8]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0 })
        }

        // trainで位置埋め込みを更新
        // weight -= rate * delta.average() = [[1, 2], [3, 4]] - 0.1 * [[2, 4], [6, 8]]
        //                                   = [[1, 2], [3, 4]] - [[0.2, 0.4], [0.6, 0.8]]
        //                                   = [[0.8, 1.6], [2.4, 3.2]]
        positionEmbedding._train(input, calcDelta)

        // 更新後のexpect結果
        // output = input + weight = [[5, 6], [7, 8]] + [[0.8, 1.6], [2.4, 3.2]]
        //                         = [[5.8, 7.6], [9.4, 11.2]]
        val afterOutput = positionEmbedding._expect(input)[0] as IOType.D2

        assertEquals(expected = 5.8, actual = afterOutput[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 7.6, actual = afterOutput[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.4, actual = afterOutput[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 11.2, actual = afterOutput[1, 1], absoluteTolerance = 1e-10)
    }
}
