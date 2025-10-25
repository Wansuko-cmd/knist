@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.bias

import com.wsr.IOType
import com.wsr.layer.process.bias.BiasD3
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
            optimizer = Sgd(rate = 0.1).d3(2, 2, 2),
            weight = IOType.d3(2, 2, 2) { _, _, _ -> 1.0 },
        )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        val result = bias._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        // 各要素に1.0が加算される
        assertEquals(expected = 2.0, actual = output[0, 0, 0])
        assertEquals(expected = 3.0, actual = output[0, 0, 1])
        assertEquals(expected = 4.0, actual = output[0, 1, 0])
        assertEquals(expected = 5.0, actual = output[0, 1, 1])
        assertEquals(expected = 6.0, actual = output[1, 0, 0])
        assertEquals(expected = 7.0, actual = output[1, 0, 1])
        assertEquals(expected = 8.0, actual = output[1, 1, 0])
        assertEquals(expected = 9.0, actual = output[1, 1, 1])
    }

    @Test
    fun `BiasD3の_train=deltaをそのまま返し、バイアスを更新`() {
        val initialWeight = IOType.d3(2, 2, 2) { _, _, _ -> 1.0 }
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(rate = 0.1).d3(2, 2, 2),
            weight = initialWeight,
        )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0 })
        }

        val resultDelta = bias._train(input, calcDelta)

        // deltaはそのまま返される
        assertEquals(expected = 1, actual = resultDelta.size)
        val dx = resultDelta[0] as IOType.D3
        for (x in 0 until 2) {
            for (y in 0 until 2) {
                for (z in 0 until 2) {
                    assertEquals(expected = 1.0, actual = dx[x, y, z])
                }
            }
        }

        // biasが更新されていることを確認（expectで確認）
        val outputAfter = bias._expect(input)
        val afterOutput = outputAfter[0] as IOType.D3

        // 初期weight=1.0, delta平均=1.0, rate=0.1
        // 新しいweight = 1.0 - 0.1 * 1.0 = 0.9
        // output = input + 0.9
        assertEquals(expected = 1.9, actual = afterOutput[0, 0, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 2.9, actual = afterOutput[0, 0, 1], absoluteTolerance = 1e-6)
    }

    @Test
    fun `BiasD3の_train=バイアスが更新され、期待通りの出力になる`() {
        // weight = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val weight = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }
        val bias = BiasD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
            optimizer = Sgd(rate = 0.1).d3(2, 2, 2),
            weight = weight,
        )

        // input = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // deltaは[[[2, 4], [6, 8]], [[10, 12], [14, 16]]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { x, y, z -> ((x * 4 + y * 2 + z) + 1) * 2.0 })
        }

        // trainでバイアスを更新
        // weight -= rate * delta.average() = [[[ 1,  2], [ 3,  4]], [[ 5,  6], [ 7,  8]]] - 0.1 * [[[2, 4], [6, 8]], [[10, 12], [14, 16]]]
        //                                   = [[[ 1,  2], [ 3,  4]], [[ 5,  6], [ 7,  8]]] - [[[0.2, 0.4], [0.6, 0.8]], [[1.0, 1.2], [1.4, 1.6]]]
        //                                   = [[[0.8, 1.6], [2.4, 3.2]], [[4.0, 4.8], [5.6, 6.4]]]
        bias._train(input, calcDelta)

        // 更新後のexpect結果
        // output = input + weight = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]] + [[[0.8, 1.6], [2.4, 3.2]], [[4.0, 4.8], [5.6, 6.4]]]
        //                         = [[[1.8, 3.6], [5.4, 7.2]], [[9.0, 10.8], [12.6, 14.4]]]
        val afterOutput = bias._expect(input)[0] as IOType.D3

        assertEquals(expected = 1.8, actual = afterOutput[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 3.6, actual = afterOutput[0, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 5.4, actual = afterOutput[0, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 7.2, actual = afterOutput[0, 1, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.0, actual = afterOutput[1, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 10.8, actual = afterOutput[1, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 12.6, actual = afterOutput[1, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 14.4, actual = afterOutput[1, 1, 1], absoluteTolerance = 1e-10)
    }
}
