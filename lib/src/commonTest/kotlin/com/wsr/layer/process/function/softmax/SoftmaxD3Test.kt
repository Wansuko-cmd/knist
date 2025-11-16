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
        // exp([1-3, 2-3, 3-3]) = exp([-2, -1, 0]) = [0.135f, 0.368f, 1.0f]
        // sum = 1.503f
        // softmax = [0.090f, 0.245f, 0.665f]
        assertEquals(expected = 0.09f, actual = output[0, 0, 0], absoluteTolerance = 1e-2f)
        assertEquals(expected = 0.245f, actual = output[0, 0, 1], absoluteTolerance = 1e-2f)
        assertEquals(expected = 0.665f, actual = output[0, 0, 2], absoluteTolerance = 1e-2f)

        // 確率の合計は1
        val sum = output[0, 0, 0] + output[0, 0, 1] + output[0, 0, 2]
        assertEquals(expected = 1.0f, actual = sum, absoluteTolerance = 1e-6f)
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
            listOf(IOType.d3(1, 1, 2) { _, _, _ -> 1.0f })
        }

        val result = softmax._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3

        // softmax'(x) = softmax(x) * (1 - softmax(x)) when using delta
        // 出力を計算
        val max = 2.0f
        val e1 = exp(1.0f - max) // exp(-1) ≈ 0.368f
        val e2 = exp(2.0f - max) // exp(0) = 1.0f
        val sum = e1 + e2 // ≈ 1.368f
        val s1 = e1 / sum // ≈ 0.269f
        val s2 = e2 / sum // ≈ 0.731f

        val expected1 = s1 * (1.0f - s1)
        val expected2 = s2 * (1.0f - s2)

        assertEquals(expected = expected1, actual = dx[0, 0, 0], absoluteTolerance = 1e-3f)
        assertEquals(expected = expected2, actual = dx[0, 0, 1], absoluteTolerance = 1e-3f)
    }
}
