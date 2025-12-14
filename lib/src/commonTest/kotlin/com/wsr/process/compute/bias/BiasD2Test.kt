@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.bias

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.process.Context
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class BiasD2Test {
    @Test
    fun `BiasD2の_expect=入力にバイアスを足した値を返す`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val bias =
            BiasD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // [[1+1, 2+2], [3+3, 4+4]] = [[2, 4], [6, 8]]
        val result = bias._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0, 0])
        assertEquals(expected = 4.0f, actual = output[0, 1])
        assertEquals(expected = 6.0f, actual = output[1, 0])
        assertEquals(expected = 8.0f, actual = output[1, 1])
    }

    @Test
    fun `BiasD2の_train=deltaをそのまま返し、バイアスを更新`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val bias =
            BiasD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        val result = bias._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0]
        // deltaは[[2, 4], [6, 8]]
        assertEquals(expected = 2.0f, actual = delta[0, 0])
        assertEquals(expected = 4.0f, actual = delta[0, 1])
        assertEquals(expected = 6.0f, actual = delta[1, 0])
        assertEquals(expected = 8.0f, actual = delta[1, 1])
    }

    @Test
    fun `BiasD2の_train=バイアスが更新され、期待通りの出力になる`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val bias =
            BiasD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        // trainでバイアスを更新
        // weight -= rate * delta.average() = [[1, 2], [3, 4]] - 0.1f * [[2, 4], [6, 8]]
        //                                   = [[1, 2], [3, 4]] - [[0.2f, 0.4f], [0.6f, 0.8f]]
        //                                   = [[0.8f, 1.6f], [2.4f, 3.2f]]
        bias._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後のexpect結果
        // output = input + weight = [[1, 2], [3, 4]] + [[0.8f, 1.6f], [2.4f, 3.2f]]
        //                         = [[1.8f, 3.6f], [5.4f, 7.2f]]
        val afterOutput = bias._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 1.8f, actual = afterOutput[0][0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 3.6f, actual = afterOutput[0][0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 5.4f, actual = afterOutput[0][1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 7.2f, actual = afterOutput[0][1, 1], absoluteTolerance = 1e-6f)
    }
}
