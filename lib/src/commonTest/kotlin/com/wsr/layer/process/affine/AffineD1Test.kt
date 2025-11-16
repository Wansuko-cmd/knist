@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.affine

import com.wsr.IOType
import com.wsr.layer.process.affine.AffineD1
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class AffineD1Test {
    @Test
    fun `AffineD1の_expect=重み行列との積を返す`() {
        // 入力2次元、出力3次元
        // weight = [[1, 2, 3],
        //           [4, 5, 6]]
        val weight = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val affine =
            AffineD1(
                outputSize = 3,
                optimizer = Sgd(0.1f).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // [[1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f)),
            )

        // weight.transpose() dot input
        // [[1, 4],   [[1],     [[1*1+4*2],   [[9],
        //  [2, 5],    [2]]  =   [2*1+5*2],  =  [12],
        //  [3, 6]]              [3*1+6*2]]     [15]]
        val result = affine._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 9.0f, actual = output[0])
        assertEquals(expected = 12.0f, actual = output[1])
        assertEquals(expected = 15.0f, actual = output[2])
    }

    @Test
    fun `AffineD1の_train=逆伝播を計算して重みを更新`() {
        // weight = [[1, 2],
        //           [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val affine =
            AffineD1(
                outputSize = 2,
                optimizer = Sgd(0.1f).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // [[1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f)),
            )

        // deltaは[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0f, 1.0f)))
        }

        val result = affine._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        // dx = weight dot delta = [[1,2],[3,4]] dot [[1],[1]] = [[3],[7]]
        assertEquals(expected = 3.0f, actual = dx[0])
        assertEquals(expected = 7.0f, actual = dx[1])
    }

    @Test
    fun `AffineD1の_train=重みが更新され、期待通りの出力になる`() {
        // weight = [[1, 2],
        //           [3, 4]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val affine =
            AffineD1(
                outputSize = 2,
                optimizer = Sgd(0.1f).d2(
                    x = weight.shape[0],
                    y = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [1, 2]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f)),
            )

        // deltaは[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0f, 1.0f)))
        }

        // trainで重みを更新
        // dw = input.transpose() dot delta = [[1], [2]] dot [[1, 1]] = [[1, 1], [2, 2]]
        // weight -= 0.1f / 1 * dw = [[1, 2], [3, 4]] - [[0.1f, 0.1f], [0.2f, 0.2f]]
        //                         = [[0.9f, 1.9f], [2.8f, 3.8f]]
        affine._train(input, calcDelta)

        // 更新後のexpect結果
        // output = weight.transpose() dot input
        //        = [[0.9f, 2.8f], [1.9f, 3.8f]] dot [[1], [2]]
        //        = [[0.9f*1 + 2.8f*2], [1.9f*1 + 3.8f*2]]
        //        = [[6.5f], [9.5f]]
        val afterOutput = affine._expect(input)[0] as IOType.D1

        assertEquals(
            expected = 6.5f,
            actual = afterOutput[0],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 9.5f,
            actual = afterOutput[1],
            absoluteTolerance = 1e-6f,
        )
    }
}
