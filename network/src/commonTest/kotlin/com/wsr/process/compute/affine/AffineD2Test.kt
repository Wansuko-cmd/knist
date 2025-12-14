@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.affine

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

class AffineD2Test {
    @Test
    fun `AffineD2の_expect=チャネルごとに重み行列との積を計算`() {
        // channel=2, inputSize=2, outputSize=2
        // weight = [[1, 2], [3, 4]] (全チャンネルで共有)
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val affine =
            AffineD2(
                channel = 2,
                outputSize = 2,
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

        val result = affine._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])

        // weight.transpose() = [[1, 3], [2, 4]]
        // output[0] = [[1, 3], [2, 4]] · [[1], [2]] = [[7], [10]]
        // output[1] = [[1, 3], [2, 4]] · [[3], [4]] = [[15], [22]]
        assertEquals(expected = 7.0f, actual = output[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 10.0f, actual = output[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 15.0f, actual = output[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 22.0f, actual = output[1, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `AffineD2の_train=逆伝播を計算`() {
        // channel=1, inputSize=2, outputSize=2
        // weight = [[1, 2], [3, 4]] (全チャンネルで共有)
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val affine =
            AffineD2(
                channel = 1,
                outputSize = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[1, 2]]
        val input =
            batchOf(
                IOType.d2(1, 2) { _, y -> (y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(1, 2) { _, _ -> 1.0f })
        }

        val result = affine._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 1, actual = dx.shape[0])
        assertEquals(expected = 2, actual = dx.shape[1])

        // dx[0] = weight · delta[0] = [[1, 2], [3, 4]] · [[1], [1]] = [[3], [7]]
        assertEquals(expected = 3.0f, actual = dx[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 7.0f, actual = dx[0, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `AffineD2の_train=重みが更新され、期待通りの出力になる`() {
        // channel=1, weight = [[1, 2], [3, 4]] (全チャンネルで共有)
        val weight = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val affine =
            AffineD2(
                channel = 1,
                outputSize = 2,
                optimizer = Sgd(Scheduler.Fix(0.1f)).d2(
                    i = weight.shape[0],
                    j = weight.shape[1],
                ),
                weight = weight,
            )

        // input = [[1, 2]]
        val input =
            batchOf(
                IOType.d2(1, 2) { _, y -> (y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(1, 2) { _, _ -> 1.0f })
        }

        // trainで重みを更新
        // dw = input[0].transpose() · delta[0] = [[1], [2]] · [[1, 1]] = [[1, 1], [2, 2]]
        // weight -= 0.1f * dw = [[1, 2], [3, 4]] - [[0.1f, 0.1f], [0.2f, 0.2f]]
        //                    = [[0.9f, 1.9f], [2.8f, 3.8f]]
        affine._train(input, context, calcDelta) as Batch<IOType.D2>
        // 更新後のexpect結果
        // output[0] = weight.transpose() · input[0]
        //           = [[0.9f, 2.8f], [1.9f, 3.8f]] · [[1], [2]]
        //           = [[6.5f], [9.5f]]
        val afterOutput = affine._expect(input, context) as Batch<IOType.D2>

        assertEquals(expected = 6.5f, actual = afterOutput[0][0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 9.5f, actual = afterOutput[0][0, 1], absoluteTolerance = 1e-6f)
    }
}
