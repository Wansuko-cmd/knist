@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.bias

import com.wsr.IOType
import com.wsr.layer.process.bias.BiasD2
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
                optimizer = Sgd(0.1).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )

        // [[1+1, 2+2], [3+3, 4+4]] = [[2, 4], [6, 8]]
        val result = bias._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 2.0, actual = output[0, 0])
        assertEquals(expected = 4.0, actual = output[0, 1])
        assertEquals(expected = 6.0, actual = output[1, 0])
        assertEquals(expected = 8.0, actual = output[1, 1])
    }

    @Test
    fun `BiasD2の_train=deltaをそのまま返し、バイアスを更新`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val bias =
            BiasD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0 })
        }

        val result = bias._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val delta = result[0] as IOType.D2
        // deltaは[[2, 4], [6, 8]]
        assertEquals(expected = 2.0, actual = delta[0, 0])
        assertEquals(expected = 4.0, actual = delta[0, 1])
        assertEquals(expected = 6.0, actual = delta[1, 0])
        assertEquals(expected = 8.0, actual = delta[1, 1])
    }

    @Test
    fun `BiasD2の_train=バイアスが更新され、期待通りの出力になる`() {
        // weight = [[1, 2], [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val bias =
            BiasD2(
                outputX = 2,
                outputY = 2,
                optimizer = Sgd(0.1).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0 })
        }

        // trainでバイアスを更新
        // weight -= rate * delta.average() = [[1, 2], [3, 4]] - 0.1 * [[2, 4], [6, 8]]
        //                                   = [[1, 2], [3, 4]] - [[0.2, 0.4], [0.6, 0.8]]
        //                                   = [[0.8, 1.6], [2.4, 3.2]]
        bias._train(input, calcDelta)

        // 更新後のexpect結果
        // output = input + weight = [[1, 2], [3, 4]] + [[0.8, 1.6], [2.4, 3.2]]
        //                         = [[1.8, 3.6], [5.4, 7.2]]
        val afterOutput = bias._expect(input)[0] as IOType.D2

        assertEquals(expected = 1.8, actual = afterOutput[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 3.6, actual = afterOutput[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 5.4, actual = afterOutput[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 7.2, actual = afterOutput[1, 1], absoluteTolerance = 1e-10)
    }
}
