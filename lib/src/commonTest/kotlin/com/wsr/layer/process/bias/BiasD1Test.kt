@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.bias

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.layer.Context
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class BiasD1Test {
    @Test
    fun `BiasD1の_expect=入力にバイアスを足した値を返す`() {
        // weight = [1, 2, 3]
        val weight = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val bias =
            BiasD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d1(size = weight.shape[0]),
                weight = weight,
            )

        // [[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
                IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
            )
        val context = Context(input)

        // [[1+1, 2+2, 3+3], [4+1, 5+2, 6+3]] = [[2, 4, 6], [5, 7, 9]]
        val result = bias._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = 2, actual = result.size)
        val output0 = result[0]
        assertEquals(expected = 2.0f, actual = output0[0])
        assertEquals(expected = 4.0f, actual = output0[1])
        assertEquals(expected = 6.0f, actual = output0[2])

        val output1 = result[1]
        assertEquals(expected = 5.0f, actual = output1[0])
        assertEquals(expected = 7.0f, actual = output1[1])
        assertEquals(expected = 9.0f, actual = output1[2])
    }

    @Test
    fun `BiasD1の_train=deltaをそのまま返し、バイアスを更新`() {
        // weight = [1, 2, 3]
        val weight = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val bias =
            BiasD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d1(size = weight.shape[0]),
                weight = weight,
            )

        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        // deltaは[2, 4, 6]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f)))
        }

        val result = bias._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val delta = result[0]
        assertEquals(expected = 2.0f, actual = delta[0])
        assertEquals(expected = 4.0f, actual = delta[1])
        assertEquals(expected = 6.0f, actual = delta[2])
    }

    @Test
    fun `BiasD1の_train=バイアスが更新され、期待通りの出力になる`() {
        // weight = [1, 2, 3]
        val weight = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val bias =
            BiasD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d1(size = weight.shape[0]),
                weight = weight,
            )

        // input = [1, 2, 3]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        // deltaは[2, 4, 6]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f)))
        }

        // trainでバイアスを更新
        // weight -= rate * delta.average() = [1, 2, 3] - 0.1f * [2, 4, 6]
        //                                   = [1, 2, 3] - [0.2f, 0.4f, 0.6f]
        //                                   = [0.8f, 1.6f, 2.4f]
        bias._train(input, context, calcDelta) as Batch<IOType.D1>
        // 更新後のexpect結果
        // output = input + weight = [1, 2, 3] + [0.8f, 1.6f, 2.4f] = [1.8f, 3.6f, 5.4f]
        val afterOutput = bias._expect(input, context) as Batch<IOType.D1>

        assertEquals(
            expected = 1.8f,
            actual = afterOutput[0][0],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 3.6f,
            actual = afterOutput[0][1],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 5.4f,
            actual = afterOutput[0][2],
            absoluteTolerance = 1e-6f,
        )
    }
}
