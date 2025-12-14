@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.position

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import com.wsr.process.Context
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
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // 入力は[[5, 6], [7, 8]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val context = Context(input)

        // 出力 = 入力 + 位置埋め込み
        // [[5, 6], [7, 8]] + [[1, 2], [3, 4]] = [[6, 8], [10, 12]]
        val result = positionEmbedding._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
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
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[5, 6], [7, 8]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val context = Context(input)

        // deltaは[[0.1f, 0.2f], [0.3f, 0.4f]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> (x * 2 + y + 1) * 0.1f })
        }

        val result = positionEmbedding._train(input, context, calcDelta) as Batch<IOType.D2>
        // 位置埋め込みの逆伝播は、加算なのでdeltaをそのまま返す
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0]
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
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[5, 6], [7, 8]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        // trainで位置埋め込みを更新
        // weight -= rate * delta.average() = [[1, 2], [3, 4]] - 0.1f * [[2, 4], [6, 8]]
        //                                   = [[1, 2], [3, 4]] - [[0.2f, 0.4f], [0.6f, 0.8f]]
        //                                   = [[0.8f, 1.6f], [2.4f, 3.2f]]
        positionEmbedding._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後のexpect結果
        // output = input + weight = [[5, 6], [7, 8]] + [[0.8f, 1.6f], [2.4f, 3.2f]]
        //                         = [[5.8f, 7.6f], [9.4f, 11.2f]]
        val afterOutput = positionEmbedding._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 5.8f, actual = afterOutput[0][0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 7.6f, actual = afterOutput[0][0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 9.4f, actual = afterOutput[0][1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 11.2f, actual = afterOutput[0][1, 1], absoluteTolerance = 1e-6f)
    }
}
