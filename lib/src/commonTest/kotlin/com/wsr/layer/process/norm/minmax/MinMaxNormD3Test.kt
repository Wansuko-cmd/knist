@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.norm.minmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxNormD3Test {
    @Test
    fun `MinMaxNormD3の_expect=min-max正規化を適用`() {
        // alpha = [[[1, 1], [1, 1]], [[1, 1], [1, 1]]]
        val alpha = IOType.d3(2, 2, 2) { _, _, _ -> 1.0f }
        val norm =
            MinMaxNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1f).d3(x = 2, y = 2, z = 2),
                weight = alpha,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = norm._expect(input, context) as Batch<IOType.D3>
        // min=1, max=8, denominator=7
        // output = alpha * (input - min) / denominator
        // [[[0, 1/7], [2/7, 3/7]], [[4/7, 5/7], [6/7, 1]]]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        assertEquals(expected = 0.0f, actual = output[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f / 7.0f, actual = output[0, 0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 2.0f / 7.0f, actual = output[0, 1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 3.0f / 7.0f, actual = output[0, 1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 4.0f / 7.0f, actual = output[1, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 5.0f / 7.0f, actual = output[1, 0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 6.0f / 7.0f, actual = output[1, 1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f, actual = output[1, 1, 1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `MinMaxNormD3の_train=正規化の微分を計算し、alphaを更新`() {
        val alpha = IOType.d3(2, 2, 2) { _, _, _ -> 1.0f }
        val norm =
            MinMaxNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1f).d3(x = 2, y = 2, z = 2),
                weight = alpha,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[[1, 1], [1, 1]], [[1, 1], [1, 1]]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0f })
        }

        val result = norm._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
        // 2x2x2のdxが返される
        assertEquals(expected = 2, actual = dx.shape[0])
        assertEquals(expected = 2, actual = dx.shape[1])
        assertEquals(expected = 2, actual = dx.shape[2])
    }

    @Test
    fun `MinMaxNormD3の_train=alphaが更新され、期待通りの出力になる`() {
        // alpha = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]]
        val alpha = IOType.d3(2, 2, 2) { _, _, _ -> 2.0f }
        val norm =
            MinMaxNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1f).d3(x = 2, y = 2, z = 2),
                weight = alpha,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]] - min=1, max=8
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[[1, 1], [1, 1]], [[1, 1], [1, 1]]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0f })
        }

        // trainでalphaを更新
        // mean = [[[0, 1/7], [2/7, 3/7]], [[4/7, 5/7], [6/7, 1]]]
        // alpha -= 0.1f * mean * delta
        //        = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]] - 0.1f * [[[0*1, 1/7*1], [2/7*1, 3/7*1]], [[4/7*1, 5/7*1], [6/7*1, 1*1]]]
        //        = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]] - [[[0, 0.014286f], [0.028571f, 0.042857f]], [[0.057143f, 0.071429f], [0.085714f, 0.1f]]]
        //        = [[[2, 1.985714f], [1.971429f, 1.957143f]], [[1.942857f, 1.928571f], [1.914286f, 1.9f]]]
        norm._train(input, context, calcDelta) as Batch<IOType.D3>
        // 更新後のexpect結果
        // output = alpha * (input - min) / (max - min)
        val afterOutput = norm._expect(input, context) as Batch<IOType.D3>

        assertEquals(expected = 0.0f, actual = afterOutput[0][0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(
            expected = 0.2836734693877551f,
            actual = afterOutput[0][0, 0, 1],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 0.5632653061224490f,
            actual = afterOutput[0][0, 1, 0],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 0.8387755102040816f,
            actual = afterOutput[0][0, 1, 1],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(expected = 1.110204081632653f, actual = afterOutput[0][1, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(
            expected = 1.3775510204081634f,
            actual = afterOutput[0][1, 0, 1],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(
            expected = 1.6408163265306124f,
            actual = afterOutput[0][1, 1, 0],
            absoluteTolerance = 1e-6f,
        )
        assertEquals(expected = 1.9f, actual = afterOutput[0][1, 1, 1], absoluteTolerance = 1e-6f)
    }
}
