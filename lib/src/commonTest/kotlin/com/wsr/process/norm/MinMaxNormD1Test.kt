@file:Suppress("NonAsciiCharacters")

package com.wsr.process.norm

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxNormD1Test {
    @Test
    fun `MinMaxNormD1の_expect=min-max正規化を適用`() {
        // alpha = [1, 1, 1]
        val alpha = IOType.d1(listOf(1.0, 1.0, 1.0))
        val norm =
            MinMaxNormD1(
                outputSize = 3,
                rate = 0.1,
                weight = alpha,
            )

        // [1, 2, 5]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 5.0)),
            )

        val result = norm._expect(input)

        // min=1, max=5, denominator=4
        // output = alpha * (input - min) / denominator
        // [(1-1)/4, (2-1)/4, (5-1)/4] = [0, 0.25, 1]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 0.0, actual = output[0], absoluteTolerance = 1e-4)
        assertEquals(expected = 0.25, actual = output[1], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0, actual = output[2], absoluteTolerance = 1e-4)
    }

    @Test
    fun `MinMaxNormD1の_train=正規化の微分を計算し、alphaを更新`() {
        val alpha = IOType.d1(listOf(1.0, 1.0, 1.0))
        val norm =
            MinMaxNormD1(
                outputSize = 3,
                rate = 0.1,
                weight = alpha,
            )

        // [1, 2, 5]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 5.0)),
            )

        // deltaは[1, 1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0, 1.0, 1.0)))
        }

        val result = norm._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        // 3要素のdxが返される
        assertEquals(expected = 3, actual = dx.value.size)
    }

    @Test
    fun `MinMaxNormD1の_train=alphaが更新され、期待通りの出力になる`() {
        // alpha = [2, 2, 2]
        val alpha = IOType.d1(listOf(2.0, 2.0, 2.0))
        val norm =
            MinMaxNormD1(
                outputSize = 3,
                rate = 0.1,
                weight = alpha,
            )

        // [1, 3, 5] - min=1, max=5
        val input =
            listOf(
                IOType.d1(listOf(1.0, 3.0, 5.0)),
            )

        // deltaは[1, 1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0, 1.0, 1.0)))
        }

        // trainでalphaを更新
        // mean = [(1-1)/4, (3-1)/4, (5-1)/4] = [0, 0.5, 1]
        // alpha -= 0.1 * mean * delta = [2, 2, 2] - 0.1 * [0*1, 0.5*1, 1*1]
        //                              = [2, 2, 2] - [0, 0.05, 0.1]
        //                              = [2, 1.95, 1.9]
        norm._train(input, calcDelta)

        // 更新後のexpect結果
        // output = alpha * (input - min) / (max - min)
        //        = [2, 1.95, 1.9] * [0, 0.5, 1]
        //        = [0, 0.975, 1.9]
        val afterOutput = norm._expect(input)[0] as IOType.D1

        assertEquals(expected = 0.0, actual = afterOutput[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.975, actual = afterOutput[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 1.9, actual = afterOutput[2], absoluteTolerance = 1e-10)
    }
}
