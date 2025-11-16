@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.softmax

import com.wsr.IOType
import com.wsr.layer.process.function.softmax.SoftmaxD3
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxD3Test {
    @Test
    fun `SoftmaxD3の_expect=確率分布を計算する`() {
        val softmax = SoftmaxD3(outputX = 1, outputY = 1, outputZ = 3)

        // [[[1, 2, 3]]]
        val input =
            listOf(
                IOType.d3(1, 1, 3) { _, _, z -> (z + 1).toFloat() },
            )

        val result = softmax._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3

        // softmax(x_i) = exp(x_i - max) / sum(exp(x_j - max))
        // max = 3
        // exp([1-3, 2-3, 3-3]) = exp([-2, -1, 0]) = [0.135, 0.368, 1.0]
        // sum = 1.503
        // softmax = [0.090, 0.245, 0.665]
        assertEquals(expected = 0.09, actual = output[0, 0, 0], absoluteTolerance = 1e-2)
        assertEquals(expected = 0.245, actual = output[0, 0, 1], absoluteTolerance = 1e-2)
        assertEquals(expected = 0.665, actual = output[0, 0, 2], absoluteTolerance = 1e-2)

        // 確率の合計は1
        val sum = output[0, 0, 0] + output[0, 0, 1] + output[0, 0, 2]
        assertEquals(expected = 1.0, actual = sum, absoluteTolerance = 1e-6)
    }

    @Test
    fun `SoftmaxD3の_train=Softmaxの微分を正しく計算する`() {
        val softmax = SoftmaxD3(outputX = 1, outputY = 1, outputZ = 2)

        // [[[1, 2]]]
        val input =
            listOf(
                IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(1, 1, 2) { _, _, _ -> 1.0 })
        }

        val result = softmax._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3

        // softmax'(x) = softmax(x) * (1 - softmax(x)) when using delta
        // 出力を計算
        val max = 2.0
        val e1 = exp(1.0 - max) // exp(-1) ≈ 0.368
        val e2 = exp(2.0 - max) // exp(0) = 1.0
        val sum = e1 + e2 // ≈ 1.368
        val s1 = e1 / sum // ≈ 0.269
        val s2 = e2 / sum // ≈ 0.731

        val expected1 = s1 * (1.0 - s1)
        val expected2 = s2 * (1.0 - s2)

        assertEquals(expected = expected1, actual = dx[0, 0, 0], absoluteTolerance = 1e-3)
        assertEquals(expected = expected2, actual = dx[0, 0, 1], absoluteTolerance = 1e-3)
    }
}
