@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.softmax

import com.wsr.IOType
import com.wsr.output.softmax.SoftmaxWithLossD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxWithLossD1Test {
    @Test
    fun `SoftmaxWithLossD1の_expect=softmaxを適用した値を返す`() {
        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3, temperature = 1.0f)
        val result = softmax._expect(input)

        // max = 3
        // exp(1-3) = exp(-2)
        // exp(2-3) = exp(-1)
        // exp(3-3) = exp(0) = 1
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(
            expected = exp0 / sum,
            actual = output[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = exp1 / sum,
            actual = output[1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = exp2 / sum,
            actual = output[2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD1の_train=softmax適用後にラベルを引いた値を返す`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        // [[0, 0, 1]]
        val label =
            listOf(
                IOType.d1(listOf(0.0f, 0.0f, 1.0f)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3, temperature = 1.0f)
        val result = softmax._train(input, label)

        // max = 3
        // exp(1-3) = exp(-2)
        // exp(2-3) = exp(-1)
        // exp(3-3) = exp(0) = 1
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        // [exp0/sum - 0, exp1/sum - 0, exp2/sum - 1]
        val output = result[0] as IOType.D1
        assertEquals(
            expected = (exp0 / sum - 0f),
            actual = output[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0f),
            actual = output[1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1f),
            actual = output[2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD1の_train=maskValueで指定した値の要素は勾配が0になる`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        // [[0, 0, -1]] ← 要素2はマスク対象
        val label =
            listOf(
                IOType.d1(listOf(0.0f, 0.0f, -1.0f)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3, temperature = 1.0f, maskValue = -1)
        val result = softmax._train(input, label)

        // softmax計算
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1

        // 要素0, 1: 通常の勾配
        assertEquals(
            expected = (exp0 / sum - 0f),
            actual = output[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0f),
            actual = output[1],
            absoluteTolerance = 1e-4f,
        )

        // 要素2: maskValue=-1なので勾配は0
        assertEquals(expected = 0.0f, actual = output[2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SoftmaxWithLossD1の_train=maskValue=nullの場合は全要素が有効`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        // [[-1, -1, 1]] ← -1を含むがmaskValue=nullなので無視されない
        val label =
            listOf(
                IOType.d1(listOf(-1.0f, -1.0f, 1.0f)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3, temperature = 1.0f, maskValue = null)
        val result = softmax._train(input, label)

        // softmax計算
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1

        // 全要素が有効なので通常の勾配
        assertEquals(
            expected = (exp0 / sum - (-1f)),
            actual = output[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - (-1f)),
            actual = output[1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1f),
            actual = output[2],
            absoluteTolerance = 1e-4f,
        )
    }
}
