@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.norm.minmax

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxNormD2Test {
    @Test
    fun `MinMaxNormD2の_expect=min-max正規化を適用`() {
        // alpha = [[1, 1], [1, 1]]
        val alpha = IOType.d2(2, 2) { _, _ -> 1.0f }
        val norm =
            MinMaxNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(i = 2, j = 2),
                weight = alpha,
            )

        // [[1, 2], [3, 5]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> listOf(listOf(1.0f, 2.0f), listOf(3.0f, 5.0f))[x][y] },
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D2>
        // min=1, max=5, denominator=4
        // output = alpha * (input - min) / denominator
        // [[(1-1)/4, (2-1)/4], [(3-1)/4, (5-1)/4]] = [[0, 0.25f], [0.5f, 1]]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 0.0f, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.25f, actual = output[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.5f, actual = output[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f, actual = output[1, 1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `MinMaxNormD2の_train=正規化の微分を計算し、alphaを更新`() {
        val alpha = IOType.d2(2, 2) { _, _ -> 1.0f }
        val norm =
            MinMaxNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(i = 2, j = 2),
                weight = alpha,
            )

        // [[1, 2], [3, 5]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> listOf(listOf(1.0f, 2.0f), listOf(3.0f, 5.0f))[x][y] },
            )
        val context = Context(input)

        // deltaは[[1, 1], [1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { _, _ -> 1.0f })
        }

        val result = norm._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // 2x2のdxが返される
        assertEquals(expected = 2, actual = dx.shape[0])
        assertEquals(expected = 2, actual = dx.shape[1])
    }

    @Test
    fun `MinMaxNormD2の_train=alphaが更新され、期待通りの出力になる`() {
        // alpha = [[2, 2], [2, 2]]
        val alpha = IOType.d2(2, 2) { _, _ -> 2.0f }
        val norm =
            MinMaxNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(i = 2, j = 2),
                weight = alpha,
            )

        // [[1, 2], [3, 5]] - min=1, max=5
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> listOf(listOf(1.0f, 2.0f), listOf(3.0f, 5.0f))[x][y] },
            )
        val context = Context(input)

        // deltaは[[1, 1], [1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { _, _ -> 1.0f })
        }

        // trainでalphaを更新
        // mean = [[(1-1)/4, (2-1)/4], [(3-1)/4, (5-1)/4]] = [[0, 0.25f], [0.5f, 1]]
        // alpha -= 0.1f * mean * delta = [[2, 2], [2, 2]] - 0.1f * [[0*1, 0.25f*1], [0.5f*1, 1*1]]
        //                              = [[2, 2], [2, 2]] - [[0, 0.025f], [0.05f, 0.1f]]
        //                              = [[2, 1.975f], [1.95f, 1.9f]]
        norm._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後のexpect結果
        // output = alpha * (input - min) / (max - min)
        //        = [[2, 1.975f], [1.95f, 1.9f]] * [[0, 0.25f], [0.5f, 1]]
        //        = [[0, 0.49375f], [0.975f, 1.9f]]
        val afterOutput = norm._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 0.0f, actual = afterOutput[0][0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.49375f, actual = afterOutput[0][0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.975f, actual = afterOutput[0][1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.9f, actual = afterOutput[0][1, 1], absoluteTolerance = 1e-6f)
    }
}
