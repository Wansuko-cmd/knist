@file:Suppress("NonAsciiCharacters")

package com.wsr.process.affine

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class AffineD2Test {
    @Test
    fun `AffineD2の_expect=チャネルごとに重み行列との積を計算`() {
        // channel=2, inputSize=2, outputSize=2
        // weight[0] = [[1, 2], [3, 4]]
        // weight[1] = [[5, 6], [7, 8]]
        val weight =
            IOType.d3(2, 2, 2) { c, x, y ->
                (c * 4 + x * 2 + y + 1).toDouble()
            }
        val affine =
            AffineD2(
                channel = 2,
                outputSize = 2,
                optimizer = Sgd(0.1).d3(
                    x = weight.shape[0],
                    y = weight.shape[1],
                    z = weight.shape[2],
                ),
                weight = weight,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        val result = affine._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])
    }

    @Test
    fun `AffineD2の_train=逆伝播を計算`() {
        // channel=1, inputSize=2, outputSize=2
        // weight[0] = [[1, 2], [3, 4]]
        val weight = IOType.d3(1, 2, 2) { _, x, y -> (x * 2 + y + 1).toDouble() }
        val affine =
            AffineD2(
                channel = 1,
                outputSize = 2,
                optimizer = Sgd(0.1).d3(
                    x = weight.shape[0],
                    y = weight.shape[1],
                    z = weight.shape[2],
                ),
                weight = weight,
            )

        // [[1, 2]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> (y + 1).toDouble() },
            )

        // deltaは[[1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(1, 2) { _, _ -> 1.0 })
        }

        val result = affine._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = 1, actual = dx.shape[0])
        assertEquals(expected = 2, actual = dx.shape[1])
    }

    @Test
    fun `AffineD2の_train=重みが更新され、期待通りの出力になる`() {
        // channel=1, weight[0] = [[1, 2], [3, 4]]
        val weight = IOType.d3(1, 2, 2) { _, x, y -> (x * 2 + y + 1).toDouble() }
        val affine =
            AffineD2(
                channel = 1,
                outputSize = 2,
                optimizer = Sgd(0.1).d3(
                    x = weight.shape[0],
                    y = weight.shape[1],
                    z = weight.shape[2],
                ),
                weight = weight,
            )

        // input = [[1, 2]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> (y + 1).toDouble() },
            )

        // deltaは[[1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(1, 2) { _, _ -> 1.0 })
        }

        // trainで重みを更新
        // dw[0] = input[0].transpose() dot delta[0] = [[1], [2]] dot [[1, 1]] = [[1, 1], [2, 2]]
        // weight[0] -= 0.1 / 1 * dw[0] = [[1, 2], [3, 4]] - [[0.1, 0.1], [0.2, 0.2]]
        //                               = [[0.9, 1.9], [2.8, 3.8]]
        affine._train(input, calcDelta)

        // 更新後のexpect結果
        // output[0] = weight[0].transpose() dot input[0]
        //           = [[0.9, 2.8], [1.9, 3.8]] dot [[1], [2]]
        //           = [[6.5], [9.5]]
        val afterOutput = affine._expect(input)[0] as IOType.D2

        assertEquals(expected = 6.5, actual = afterOutput[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.5, actual = afterOutput[0, 1], absoluteTolerance = 1e-10)
    }
}
