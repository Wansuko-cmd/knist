@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.bias

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class BiasD3Test {
    @Test
    fun `BiasD3の_expect=入力にバイアスを足した値を返す`() {
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(rate = 0.1f).d3(2, 2, 2),
            weight = IOType.d3(2, 2, 2) { _, _, _ -> 1.0f },
        )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = bias._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        // 各要素に1.0が加算される
        assertEquals(expected = 2.0f, actual = output[0, 0, 0])
        assertEquals(expected = 3.0f, actual = output[0, 0, 1])
        assertEquals(expected = 4.0f, actual = output[0, 1, 0])
        assertEquals(expected = 5.0f, actual = output[0, 1, 1])
        assertEquals(expected = 6.0f, actual = output[1, 0, 0])
        assertEquals(expected = 7.0f, actual = output[1, 0, 1])
        assertEquals(expected = 8.0f, actual = output[1, 1, 0])
        assertEquals(expected = 9.0f, actual = output[1, 1, 1])
    }

    @Test
    fun `BiasD3の_train=deltaをそのまま返し、バイアスを更新`() {
        val initialWeight = IOType.d3(2, 2, 2) { _, _, _ -> 1.0f }
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(rate = 0.1f).d3(2, 2, 2),
            weight = initialWeight,
        )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // 全て1のdelta
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0f })
        }

        val resultDelta = bias._train(input, context, calcDelta) as Batch<IOType.D3>
        // deltaはそのまま返される
        assertEquals(expected = 1, actual = resultDelta.size)
        val dx = resultDelta[0]
        for (x in 0 until 2) {
            for (y in 0 until 2) {
                for (z in 0 until 2) {
                    assertEquals(expected = 1.0f, actual = dx[x, y, z])
                }
            }
        }

        // biasが更新されていることを確認（expectで確認）
        val outputAfter = bias._expect(input, context) as Batch<IOType.D3>
        val afterOutput = outputAfter

        // 初期weight=1.0f, delta平均=1.0f, rate=0.1f
        // 新しいweight = 1.0f - 0.1f * 1.0f = 0.9f
        // output = input + 0.9f
        assertEquals(expected = 1.9f, actual = afterOutput[0][0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 2.9f, actual = afterOutput[0][0, 0, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `BiasD3の_train=バイアスが更新され、期待通りの出力になる`() {
        // weight = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val weight = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(rate = 0.1f).d3(2, 2, 2),
            weight = weight,
        )

        // input = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[[2, 4], [6, 8]], [[10, 12], [14, 16]]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { x, y, z -> ((x * 4 + y * 2 + z) + 1) * 2.0f })
        }

        // trainでバイアスを更新
        // weight -= rate * delta.average() = [[[ 1,  2], [ 3,  4]], [[ 5,  6], [ 7,  8]]] - 0.1f * [[[2, 4], [6, 8]], [[10, 12], [14, 16]]]
        //                                   = [[[ 1,  2], [ 3,  4]], [[ 5,  6], [ 7,  8]]] - [[[0.2f, 0.4f], [0.6f, 0.8f]], [[1.0f, 1.2f], [1.4f, 1.6f]]]
        //                                   = [[[0.8f, 1.6f], [2.4f, 3.2f]], [[4.0f, 4.8f], [5.6f, 6.4f]]]
        bias._train(input, context, calcDelta) as Batch<IOType.D3>
        // 更新後のexpect結果
        // output = input + weight = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]] + [[[0.8f, 1.6f], [2.4f, 3.2f]], [[4.0f, 4.8f], [5.6f, 6.4f]]]
        //                         = [[[1.8f, 3.6f], [5.4f, 7.2f]], [[9.0f, 10.8f], [12.6f, 14.4f]]]
        val afterOutput = bias._expect(input, context) as Batch<IOType.D3>

        assertEquals(expected = 1.8f, actual = afterOutput[0][0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 3.6f, actual = afterOutput[0][0, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 5.4f, actual = afterOutput[0][0, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 7.2f, actual = afterOutput[0][0, 1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 9.0f, actual = afterOutput[0][1, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 10.8f, actual = afterOutput[0][1, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 12.6f, actual = afterOutput[0][1, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 14.4f, actual = afterOutput[0][1, 1, 1], absoluteTolerance = 1e-6f)
    }
}
