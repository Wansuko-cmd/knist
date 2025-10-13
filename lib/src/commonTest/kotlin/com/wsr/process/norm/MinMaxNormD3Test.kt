@file:Suppress("NonAsciiCharacters")

package com.wsr.process.norm

import com.wsr.IOType
import com.wsr.optimizer.sgd.Sgd
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxNormD3Test {
    @Test
    fun `MinMaxNormD3の_expect=min-max正規化を適用`() {
        // alpha = [[[1, 1], [1, 1]], [[1, 1], [1, 1]]]
        val alpha = IOType.d3(2, 2, 2) { _, _, _ -> 1.0 }
        val norm =
            MinMaxNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1).d3(x = 2, y = 2, z = 2),
                weight = alpha,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        val result = norm._expect(input)

        // min=1, max=8, denominator=7
        // output = alpha * (input - min) / denominator
        // [[[0, 1/7], [2/7, 3/7]], [[4/7, 5/7], [6/7, 1]]]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        assertEquals(expected = 0.0, actual = output[0, 0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 / 7.0, actual = output[0, 0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 2.0 / 7.0, actual = output[0, 1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 3.0 / 7.0, actual = output[0, 1, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 4.0 / 7.0, actual = output[1, 0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 5.0 / 7.0, actual = output[1, 0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 6.0 / 7.0, actual = output[1, 1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0, actual = output[1, 1, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `MinMaxNormD3の_train=正規化の微分を計算し、alphaを更新`() {
        val alpha = IOType.d3(2, 2, 2) { _, _, _ -> 1.0 }
        val norm =
            MinMaxNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1).d3(x = 2, y = 2, z = 2),
                weight = alpha,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // deltaは[[[1, 1], [1, 1]], [[1, 1], [1, 1]]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0 })
        }

        val result = norm._train(input, calcDelta)

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
        val alpha = IOType.d3(2, 2, 2) { _, _, _ -> 2.0 }
        val norm =
            MinMaxNormD3(
                outputX = 2,
                outputY = 2,
                outputZ = 2,
                optimizer = Sgd(0.1).d3(x = 2, y = 2, z = 2),
                weight = alpha,
            )

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]] - min=1, max=8
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // deltaは[[[1, 1], [1, 1]], [[1, 1], [1, 1]]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0 })
        }

        // trainでalphaを更新
        // mean = [[[0, 1/7], [2/7, 3/7]], [[4/7, 5/7], [6/7, 1]]]
        // alpha -= 0.1 * mean * delta
        //        = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]] - 0.1 * [[[0*1, 1/7*1], [2/7*1, 3/7*1]], [[4/7*1, 5/7*1], [6/7*1, 1*1]]]
        //        = [[[2, 2], [2, 2]], [[2, 2], [2, 2]]] - [[[0, 0.014286], [0.028571, 0.042857]], [[0.057143, 0.071429], [0.085714, 0.1]]]
        //        = [[[2, 1.985714], [1.971429, 1.957143]], [[1.942857, 1.928571], [1.914286, 1.9]]]
        norm._train(input, calcDelta)

        // 更新後のexpect結果
        // output = alpha * (input - min) / (max - min)
        val afterOutput = norm._expect(input)[0] as IOType.D3

        assertEquals(expected = 0.0, actual = afterOutput[0, 0, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.2836734693877551, actual = afterOutput[0, 0, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.5632653061224490, actual = afterOutput[0, 1, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.8387755102040816, actual = afterOutput[0, 1, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = 1.110204081632653, actual = afterOutput[1, 0, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 1.3775510204081634, actual = afterOutput[1, 0, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = 1.6408163265306124, actual = afterOutput[1, 1, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 1.9, actual = afterOutput[1, 1, 1], absoluteTolerance = 1e-6)
    }
}
