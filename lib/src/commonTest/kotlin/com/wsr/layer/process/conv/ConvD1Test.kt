@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.conv

import com.wsr.IOType
import com.wsr.layer.process.conv.ConvD1
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class ConvD1Test {
    @Test
    fun `ConvD1の_expect=畳み込み処理を適用`() {
        // filter=2, channel=2, kernel=2, stride=1, padding=0
        // inputSize=3, outputSize=(3-2+0)/1+1=2
        // weight: [2, 2, 2]
        val weight =
            IOType.d3(2, 2, 2) { f, c, k ->
                (f * 4 + c * 2 + k + 1).toDouble()
            }
        // filter0: [[1, 2], [3, 4]]
        // filter1: [[5, 6], [7, 8]]

        val conv =
            ConvD1(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputSize = 3,
                optimizer = Sgd(0.1).d3(
                    x = weight.shape[0],
                    y = weight.shape[1],
                    z = weight.shape[2],
                ),
                weight = weight,
            )

        // input: [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { c, i -> (c * 3 + i + 1).toDouble() },
            )

        val result = conv._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0]) // filter
        assertEquals(expected = 2, actual = output.shape[1]) // output size
    }

    @Test
    fun `ConvD1の_train=逆畳み込みでdeltaを計算し、weightを更新`() {
        val weight =
            IOType.d3(2, 2, 2) { f, c, k ->
                (f * 4 + c * 2 + k + 1).toDouble()
            }

        val conv =
            ConvD1(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputSize = 3,
                optimizer = Sgd(0.1).d3(
                    x = weight.shape[0],
                    y = weight.shape[1],
                    z = weight.shape[2],
                ),
                weight = weight,
            )

        val input =
            listOf(
                IOType.d2(2, 3) { c, i -> (c * 3 + i + 1).toDouble() },
            )

        val calcDelta: (List<IOType>) -> List<IOType> = { output ->
            val out = output[0] as IOType.D2
            listOf(IOType.d2(out.shape) { f, i -> (f * 2 + i + 1).toDouble() })
        }

        val result = conv._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = 2, actual = dx.shape[0]) // channel
        assertEquals(expected = 3, actual = dx.shape[1]) // input size
    }

    @Test
    fun `ConvD1の_train=重みが更新され、期待通りの出力になる`() {
        val weight =
            IOType.d3(2, 2, 2) { f, c, k ->
                (f * 4 + c * 2 + k + 1).toDouble()
            }

        val conv =
            ConvD1(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputSize = 3,
                optimizer = Sgd(0.1).d3(
                    x = weight.shape[0],
                    y = weight.shape[1],
                    z = weight.shape[2],
                ),
                weight = weight,
            )

        val input =
            listOf(
                IOType.d2(2, 3) { c, i -> (c * 3 + i + 1).toDouble() },
            )

        val calcDelta: (List<IOType>) -> List<IOType> = { output ->
            val out = output[0] as IOType.D2
            listOf(IOType.d2(out.shape) { f, i -> (f * 2 + i + 1).toDouble() })
        }

        // trainで重みを更新
        conv._train(input, calcDelta)

        // 更新後のexpect結果
        // afterOutput = D2(value=[20.8, 26.4, 48.0, 64.0], shape=[2, 2])
        val afterOutput = conv._expect(input)[0] as IOType.D2

        assertEquals(expected = 20.8, actual = afterOutput[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 26.4, actual = afterOutput[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 48.0, actual = afterOutput[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 64.0, actual = afterOutput[1, 1], absoluteTolerance = 1e-10)
    }
}
