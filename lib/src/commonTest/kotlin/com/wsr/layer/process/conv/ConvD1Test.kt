@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.conv

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.optimizer.Scheduler
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
                (f * 4 + c * 2 + k + 1).toFloat()
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
                optimizer = Sgd(Scheduler.Fix(0.1f)).d3(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                ),
                weight = weight,
            )

        // input: [[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
                IOType.d2(2, 3) { c, i -> (c * 3 + i + 1).toFloat() },
            )
        val context = Context(input)

        val result = conv._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2, actual = output.shape[0]) // filter
        assertEquals(expected = 2, actual = output.shape[1]) // output size
    }

    @Test
    fun `ConvD1の_train=逆畳み込みでdeltaを計算し、weightを更新`() {
        val weight =
            IOType.d3(2, 2, 2) { f, c, k ->
                (f * 4 + c * 2 + k + 1).toFloat()
            }

        val conv =
            ConvD1(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputSize = 3,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d3(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                ),
                weight = weight,
            )

        val input =
            batchOf(
                IOType.d2(2, 3) { c, i -> (c * 3 + i + 1).toFloat() },
            )
        val context = Context(input)

        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { output ->
            batchOf(IOType.d2(output.shape) { f, i -> (f * 2 + i + 1).toFloat() })
        }

        val result = conv._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2, actual = dx.shape[0]) // channel
        assertEquals(expected = 3, actual = dx.shape[1]) // input size
    }

    @Test
    fun `ConvD1の_train=重みが更新され、期待通りの出力になる`() {
        val weight =
            IOType.d3(2, 2, 2) { f, c, k ->
                (f * 4 + c * 2 + k + 1).toFloat()
            }

        val conv =
            ConvD1(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputSize = 3,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d3(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                ),
                weight = weight,
            )

        val input =
            batchOf(
                IOType.d2(2, 3) { c, i -> (c * 3 + i + 1).toFloat() },
            )
        val context = Context(input)

        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { output ->
            batchOf(IOType.d2(output.shape) { f, i -> (f * 2 + i + 1).toFloat() })
        }

        // trainで重みを更新
        conv._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後のexpect結果
        // afterOutput = D2(value=[20.8f, 26.4f, 48.0f, 64.0f], shape=[2, 2])
        val afterOutput = conv._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 20.8f, actual = afterOutput[0][0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 26.4f, actual = afterOutput[0][0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 48.0f, actual = afterOutput[0][1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 64.0f, actual = afterOutput[0][1, 1], absoluteTolerance = 1e-5f)
    }
}
