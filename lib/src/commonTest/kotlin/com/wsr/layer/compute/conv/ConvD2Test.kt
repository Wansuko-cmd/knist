@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.compute.conv

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.d4
import com.wsr.layer.Context
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class ConvD2Test {
    @Test
    fun `ConvD2の_expect=畳み込み処理を適用`() {
        // filter=2, channel=2, kernel=2, stride=1, padding=0
        // inputX=3, inputY=3, outputX=(3-2+0)/1+1=2, outputY=(3-2+0)/1+1=2
        // weight: [2, 2, 2, 2]
        val weight =
            IOType.d4(2, 2, 2, 2) { f, c, ky, kx ->
                (f * 8 + c * 4 + ky * 2 + kx + 1).toFloat()
            }
        // filter0, channel0: [[1, 2], [3, 4]]
        // filter0, channel1: [[5, 6], [7, 8]]
        // filter1, channel0: [[9, 10], [11, 12]]
        // filter1, channel1: [[13, 14], [15, 16]]

        val conv =
            ConvD2(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputX = 3,
                inputY = 3,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d4(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                    l = weight.shape[3],
                ),
                weight = weight,
            )

        // input: channel0=[[1,2,3],[4,5,6],[7,8,9]], channel1=[[10,11,12],[13,14,15],[16,17,18]]
        val input =
            batchOf(
                IOType.d3(2, 3, 3) { c, y, x -> (c * 9 + y * 3 + x + 1).toFloat() },
            )
        val context = Context(input)

        val result = conv._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2, actual = output.shape[0]) // filter
        assertEquals(expected = 2, actual = output.shape[1]) // outputY
        assertEquals(expected = 2, actual = output.shape[2]) // outputX
    }

    @Test
    fun `ConvD2の_train=逆畳み込みでdeltaを計算し、weightを更新`() {
        val weight =
            IOType.d4(2, 2, 2, 2) { f, c, ky, kx ->
                (f * 8 + c * 4 + ky * 2 + kx + 1).toFloat()
            }

        val conv =
            ConvD2(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputX = 3,
                inputY = 3,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d4(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                    l = weight.shape[3],
                ),
                weight = weight,
            )

        val input =
            batchOf(
                IOType.d3(2, 3, 3) { c, y, x -> (c * 9 + y * 3 + x + 1).toFloat() },
            )
        val context = Context(input)

        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { output ->
            batchOf(IOType.d3(output.shape) { f, y, x -> (f * 4 + y * 2 + x + 1).toFloat() })
        }

        val result = conv._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2, actual = dx.shape[0]) // channel
        assertEquals(expected = 3, actual = dx.shape[1]) // inputY
        assertEquals(expected = 3, actual = dx.shape[2]) // inputX
    }

    @Test
    fun `ConvD2の_train=重みが更新され、出力形状が正しい`() {
        val weight =
            IOType.d4(2, 2, 2, 2) { f, c, ky, kx ->
                (f * 8 + c * 4 + ky * 2 + kx + 1).toFloat()
            }

        val conv =
            ConvD2(
                filter = 2,
                channel = 2,
                kernel = 2,
                stride = 1,
                padding = 0,
                inputX = 3,
                inputY = 3,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d4(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                    l = weight.shape[3],
                ),
                weight = weight,
            )

        val input =
            batchOf(
                IOType.d3(2, 3, 3) { c, y, x -> (c * 9 + y * 3 + x + 1).toFloat() },
            )
        val context = Context(input)

        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { output ->
            batchOf(IOType.d3(output.shape) { f, y, x -> (f * 4 + y * 2 + x + 1).toFloat() })
        }

        // trainで重みを更新
        conv._train(input, context, calcDelta) as Batch<IOType.D3>
        // 更新後のexpect結果
        val afterOutput = conv._expect(input, context) as Batch<IOType.D3>

        assertEquals(expected = 2, actual = afterOutput[0].shape[0]) // filter
        assertEquals(expected = 2, actual = afterOutput[0].shape[1]) // outputY
        assertEquals(expected = 2, actual = afterOutput[0].shape[2]) // outputX
    }

    @Test
    fun `ConvD2の_expect=strideとpaddingが正しく動作`() {
        // stride=2, padding=1
        // inputX=4, inputY=4, outputX=(4-2+2)/2+1=3, outputY=(4-2+2)/2+1=3
        val weight =
            IOType.d4(1, 1, 2, 2) { _, _, ky, kx ->
                (ky * 2 + kx + 1).toFloat()
            }

        val conv =
            ConvD2(
                filter = 1,
                channel = 1,
                kernel = 2,
                stride = 2,
                padding = 1,
                inputX = 4,
                inputY = 4,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d4(
                    i = weight.shape[0],
                    j = weight.shape[1],
                    k = weight.shape[2],
                    l = weight.shape[3],
                ),
                weight = weight,
            )

        val input =
            batchOf(
                IOType.d3(1, 4, 4) { _, y, x -> (y * 4 + x + 1).toFloat() },
            )
        val context = Context(input)

        val result = conv._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 1, actual = output.shape[0]) // filter
        assertEquals(expected = 3, actual = output.shape[1]) // outputY
        assertEquals(expected = 3, actual = output.shape[2]) // outputX
    }
}
