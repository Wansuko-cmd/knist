@file:Suppress("NonAsciiCharacters")

package com.wsr.process.bias

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class BiasD1Test {
    @Test
    fun `BiasD1の_expect=入力にバイアスを足した値を返す`() {
        // weight = [1, 2, 3]
        val weight = IOType.d1(listOf(1.0, 2.0, 3.0))
        val bias =
            BiasD1(
                outputSize = 3,
                optimizer = Sgd(0.1).d1(),
                weight = weight,
            )

        // [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
                IOType.d1(listOf(4.0, 5.0, 6.0)),
            )

        // [[1+1, 2+2, 3+3], [4+1, 5+2, 6+3]] = [[2, 4, 6], [5, 7, 9]]
        val result = bias._expect(input)

        assertEquals(expected = 2, actual = result.size)
        val output0 = result[0] as IOType.D1
        assertEquals(expected = 2.0, actual = output0[0])
        assertEquals(expected = 4.0, actual = output0[1])
        assertEquals(expected = 6.0, actual = output0[2])

        val output1 = result[1] as IOType.D1
        assertEquals(expected = 5.0, actual = output1[0])
        assertEquals(expected = 7.0, actual = output1[1])
        assertEquals(expected = 9.0, actual = output1[2])
    }

    @Test
    fun `BiasD1の_train=deltaをそのまま返し、バイアスを更新`() {
        // weight = [1, 2, 3]
        val weight = IOType.d1(listOf(1.0, 2.0, 3.0))
        val bias =
            BiasD1(
                outputSize = 3,
                optimizer = Sgd(0.1).d1(),
                weight = weight,
            )

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )

        // deltaは[2, 4, 6]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0, 4.0, 6.0)))
        }

        val result = bias._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val delta = result[0] as IOType.D1
        assertEquals(expected = 2.0, actual = delta[0])
        assertEquals(expected = 4.0, actual = delta[1])
        assertEquals(expected = 6.0, actual = delta[2])
    }

    @Test
    fun `BiasD1の_train=バイアスが更新され、期待通りの出力になる`() {
        // weight = [1, 2, 3]
        val weight = IOType.d1(listOf(1.0, 2.0, 3.0))
        val bias =
            BiasD1(
                outputSize = 3,
                optimizer = Sgd(0.1).d1(),
                weight = weight,
            )

        // input = [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )

        // deltaは[2, 4, 6]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0, 4.0, 6.0)))
        }

        // trainでバイアスを更新
        // weight -= rate * delta.average() = [1, 2, 3] - 0.1 * [2, 4, 6]
        //                                   = [1, 2, 3] - [0.2, 0.4, 0.6]
        //                                   = [0.8, 1.6, 2.4]
        bias._train(input, calcDelta)

        // 更新後のexpect結果
        // output = input + weight = [1, 2, 3] + [0.8, 1.6, 2.4] = [1.8, 3.6, 5.4]
        val afterOutput = bias._expect(input)[0] as IOType.D1

        assertEquals(expected = 1.8, actual = afterOutput[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 3.6, actual = afterOutput[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 5.4, actual = afterOutput[2], absoluteTolerance = 1e-10)
    }
}
