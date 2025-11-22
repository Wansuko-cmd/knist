@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.norm.minmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.norm.minmax.MinMaxNormD1
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxNormD1Test {
    @Test
    fun `MinMaxNormD1の_expect=min-max正規化を適用`() {
        // alpha = [1, 1, 1]
        val alpha = IOType.d1(listOf(1.0f, 1.0f, 1.0f))
        val norm =
            MinMaxNormD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d1(size = alpha.shape[0]),
                weight = alpha,
            )

        // [1, 2, 5]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 5.0f)),
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D1>
        // min=1, max=5, denominator=4
        // output = alpha * (input - min) / denominator
        // [(1-1)/4, (2-1)/4, (5-1)/4] = [0, 0.25f, 1]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 0.0f, actual = output[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.25f, actual = output[1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f, actual = output[2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `MinMaxNormD1の_train=正規化の微分を計算し、alphaを更新`() {
        val alpha = IOType.d1(listOf(1.0f, 1.0f, 1.0f))
        val norm =
            MinMaxNormD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d1(size = alpha.shape[0]),
                weight = alpha,
            )

        // [1, 2, 5]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 5.0f)),
            )
        val context = Context(input)

        // deltaは[1, 1, 1]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(1.0f, 1.0f, 1.0f)))
        }

        val result = norm._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        // 3要素のdxが返される
        assertEquals(expected = 3, actual = dx.value.size)
    }

    @Test
    fun `MinMaxNormD1の_train=alphaが更新され、期待通りの出力になる`() {
        // alpha = [2, 2, 2]
        val alpha = IOType.d1(listOf(2.0f, 2.0f, 2.0f))
        val norm =
            MinMaxNormD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d1(size = alpha.shape[0]),
                weight = alpha,
            )

        // [1, 3, 5] - min=1, max=5
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 3.0f, 5.0f)),
            )
        val context = Context(input)

        // deltaは[1, 1, 1]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(1.0f, 1.0f, 1.0f)))
        }

        // trainでalphaを更新
        // mean = [(1-1)/4, (3-1)/4, (5-1)/4] = [0, 0.5f, 1]
        // alpha -= 0.1f * mean * delta = [2, 2, 2] - 0.1f * [0*1, 0.5f*1, 1*1]
        //                              = [2, 2, 2] - [0, 0.05f, 0.1f]
        //                              = [2, 1.95f, 1.9f]
        norm._train(input, context, calcDelta) as Batch<IOType.D1>
        // 更新後のexpect結果
        // output = alpha * (input - min) / (max - min)
        //        = [2, 1.95f, 1.9f] * [0, 0.5f, 1]
        //        = [0, 0.975f, 1.9f]
        val afterOutput = norm._expect(input, context) as Batch<IOType.D1>

        assertEquals(
            expected = 0.0f,
            actual = afterOutput[0][0],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 0.975f,
            actual = afterOutput[0][1],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 1.9f,
            actual = afterOutput[0][2],
            absoluteTolerance = 1e-6f,
        )
    }
}
