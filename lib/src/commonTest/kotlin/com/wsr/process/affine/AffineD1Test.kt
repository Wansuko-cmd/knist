@file:Suppress("NonAsciiCharacters")

package com.wsr.process.affine

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AffineD1Test {
    @Test
    fun `AffineD1の_expect=重み行列との積を返す`() {
        // 入力2次元、出力3次元
        // weight = [[1, 2, 3],
        //           [4, 5, 6]]
        val weight = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() }
        val affine =
            AffineD1(
                outputSize = 3,
                rate = 0.1,
                weight = weight,
            )

        // [[1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0)),
            )

        // weight.transpose() dot input
        // [[1, 4],   [[1],     [[1*1+4*2],   [[9],
        //  [2, 5],    [2]]  =   [2*1+5*2],  =  [12],
        //  [3, 6]]              [3*1+6*2]]     [15]]
        val result = affine._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 9.0, actual = output[0])
        assertEquals(expected = 12.0, actual = output[1])
        assertEquals(expected = 15.0, actual = output[2])
    }

    @Test
    fun `AffineD1の_train=逆伝播を計算して重みを更新`() {
        // weight = [[1, 2],
        //           [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val affine =
            AffineD1(
                outputSize = 2,
                rate = 0.1,
                weight = weight,
            )

        // [[1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0)),
            )

        // deltaは[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0, 1.0)))
        }

        val result = affine._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        // dx = weight dot delta = [[1,2],[3,4]] dot [[1],[1]] = [[3],[7]]
        assertEquals(expected = 3.0, actual = dx[0])
        assertEquals(expected = 7.0, actual = dx[1])
    }

    @Test
    fun `AffineD1の_train=重みが更新され、期待通りの出力になる`() {
        // weight = [[1, 2],
        //           [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val affine =
            AffineD1(
                outputSize = 2,
                rate = 0.1,
                weight = weight,
            )

        // input = [1, 2]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0)),
            )

        // deltaは[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0, 1.0)))
        }

        // trainで重みを更新
        // dw = input.transpose() dot delta = [[1], [2]] dot [[1, 1]] = [[1, 1], [2, 2]]
        // weight -= 0.1 / 1 * dw = [[1, 2], [3, 4]] - [[0.1, 0.1], [0.2, 0.2]]
        //                         = [[0.9, 1.9], [2.8, 3.8]]
        affine._train(input, calcDelta)

        // 更新後のexpect結果
        // output = weight.transpose() dot input
        //        = [[0.9, 2.8], [1.9, 3.8]] dot [[1], [2]]
        //        = [[0.9*1 + 2.8*2], [1.9*1 + 3.8*2]]
        //        = [[6.5], [9.5]]
        val afterOutput = affine._expect(input)[0] as IOType.D1

        assertEquals(expected = 6.5, actual = afterOutput[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.5, actual = afterOutput[1], absoluteTolerance = 1e-10)
    }
}
