@file:Suppress("NonAsciiCharacters")

package com.wsr.process.norm

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxNormD2Test {
    @Test
    fun `MinMaxNormD2の_expect=min-max正規化を適用`() {
        // alpha = [[1, 1], [1, 1]]
        val alpha = IOType.d2(2, 2) { _, _ -> 1.0 }
        val norm =
            MinMaxNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = alpha,
            )

        // [[1, 2], [3, 5]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> listOf(listOf(1.0, 2.0), listOf(3.0, 5.0))[x][y] },
            )

        val result = norm._expect(input)

        // min=1, max=5, denominator=4
        // output = alpha * (input - min) / denominator
        // [[(1-1)/4, (2-1)/4], [(3-1)/4, (5-1)/4]] = [[0, 0.25], [0.5, 1]]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 0.0, actual = output[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.25, actual = output[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.5, actual = output[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0, actual = output[1, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `MinMaxNormD2の_train=正規化の微分を計算し、alphaを更新`() {
        val alpha = IOType.d2(2, 2) { _, _ -> 1.0 }
        val norm =
            MinMaxNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = alpha,
            )

        // [[1, 2], [3, 5]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> listOf(listOf(1.0, 2.0), listOf(3.0, 5.0))[x][y] },
            )

        // deltaは[[1, 1], [1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { _, _ -> 1.0 })
        }

        val result = norm._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        // 2x2のdxが返される
        assertEquals(expected = 2, actual = dx.shape[0])
        assertEquals(expected = 2, actual = dx.shape[1])
    }

    @Test
    fun `MinMaxNormD2の_train=alphaが更新され、期待通りの出力になる`() {
        // alpha = [[2, 2], [2, 2]]
        val alpha = IOType.d2(2, 2) { _, _ -> 2.0 }
        val norm =
            MinMaxNormD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(x = 2, y = 2),
                weight = alpha,
            )

        // [[1, 2], [3, 5]] - min=1, max=5
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> listOf(listOf(1.0, 2.0), listOf(3.0, 5.0))[x][y] },
            )

        // deltaは[[1, 1], [1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { _, _ -> 1.0 })
        }

        // trainでalphaを更新
        // mean = [[(1-1)/4, (2-1)/4], [(3-1)/4, (5-1)/4]] = [[0, 0.25], [0.5, 1]]
        // alpha -= 0.1 * mean * delta = [[2, 2], [2, 2]] - 0.1 * [[0*1, 0.25*1], [0.5*1, 1*1]]
        //                              = [[2, 2], [2, 2]] - [[0, 0.025], [0.05, 0.1]]
        //                              = [[2, 1.975], [1.95, 1.9]]
        norm._train(input, calcDelta)

        // 更新後のexpect結果
        // output = alpha * (input - min) / (max - min)
        //        = [[2, 1.975], [1.95, 1.9]] * [[0, 0.25], [0.5, 1]]
        //        = [[0, 0.49375], [0.975, 1.9]]
        val afterOutput = norm._expect(input)[0] as IOType.D2

        assertEquals(expected = 0.0, actual = afterOutput[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.49375, actual = afterOutput[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.975, actual = afterOutput[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 1.9, actual = afterOutput[1, 1], absoluteTolerance = 1e-10)
    }
}
