@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.softmax

import com.wsr.IOType
import com.wsr.layer.process.function.softmax.SoftmaxD2
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxD2Test {
    @Test
    fun `SoftmaxD2の_expect=確率分布を計算する`() {
        val softmax = SoftmaxD2(outputX = 1, outputY = 3)

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
            )

        val result = softmax._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // softmax([1, 2, 3]) = [exp(1), exp(2), exp(3)] / (exp(1) + exp(2) + exp(3))
        val exp1 = exp(1.0f - 3.0f) // max=3で正規化
        val exp2 = exp(2.0f - 3.0f)
        val exp3 = exp(3.0f - 3.0f)
        val sum = exp1 + exp2 + exp3

        assertEquals(expected = (exp1 / sum), actual = output[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = (exp2 / sum), actual = output[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = (exp3 / sum), actual = output[0, 2], absoluteTolerance = 1e-6f)

        // 確率の合計は1
        val totalProb = output[0, 0] + output[0, 1] + output[0, 2]
        assertEquals(expected = 1.0f, actual = totalProb, absoluteTolerance = 1e-6f)
    }

    @Test
    fun `SoftmaxD2の_train=Softmaxの微分を正しく計算する`() {
        val softmax = SoftmaxD2(outputX = 1, outputY = 2)

        // [[1, 2]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> (y + 1).toFloat() },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(1, 2) { _, _ -> 1.0f })
        }

        val result = softmax._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2

        // softmax'(x) = softmax(x) * (1 - softmax(x)) (when delta = 1)
        // softmax([1, 2])を計算
        val exp1 = exp(1.0f - 2.0f)
        val exp2 = exp(2.0f - 2.0f)
        val sum = exp1 + exp2
        val s1 = exp1 / sum
        val s2 = exp2 / sum

        // 微分: delta * softmax * (1 - softmax)
        val expected1 = 1.0f * s1 * (1.0f - s1)
        val expected2 = 1.0f * s2 * (1.0f - s2)

        assertEquals(expected = expected1, actual = dx[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = expected2, actual = dx[0, 1], absoluteTolerance = 1e-6f)
    }
}
