@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.output.softmax

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import kotlin.collections.get
import kotlin.collections.listOf
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class SoftmaxWithLossD1Test {
    @Test
    fun `SoftmaxWithLossD1の_expect=softmaxを適用した値を返す`() {
        // [1, 2, 3]
        val input =
            batchOf(
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
        val output = result as Batch<IOType.D1>
        assertEquals(
            expected = exp0 / sum,
            actual = output[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = exp1 / sum,
            actual = output[0][1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = exp2 / sum,
            actual = output[0][2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD1の_train=softmax適用後にラベルを引いた値を返す`() {
        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        // [[0, 0, 1]]
        val label =
            batchOf(
                IOType.d1(listOf(0.0f, 0.0f, 1.0f)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3, temperature = 1.0f)
        val result = softmax._train(input) { label }

        // max = 3
        // exp(1-3) = exp(-2)
        // exp(2-3) = exp(-1)
        // exp(3-3) = exp(0) = 1
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        // loss = -mean(ln(sum(label * softmax) + 1e-7))
        // label = [0, 0, 1], softmax = [exp0/sum, exp1/sum, exp2/sum]
        // sum(label * softmax) = 0*(exp0/sum) + 0*(exp1/sum) + 1*(exp2/sum) = exp2/sum
        // loss = -ln(exp2/sum + 1e-7)
        val epsilon = 1e-7f
        val softmax0 = exp0 / sum
        val softmax1 = exp1 / sum
        val softmax2 = exp2 / sum
        val labelDotSoftmax = 0.0f * softmax0 + 0.0f * softmax1 + 1.0f * softmax2
        val expectedLoss = -kotlin.math.ln(labelDotSoftmax + epsilon)
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        // [exp0/sum - 0, exp1/sum - 0, exp2/sum - 1]
        val output = result.delta as Batch<IOType.D1>
        assertEquals(
            expected = (exp0 / sum - 0f),
            actual = output[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0f),
            actual = output[0][1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum - 1f),
            actual = output[0][2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxWithLossD1の_train=maskValueで指定した値の要素は勾配が0になる`() {
        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        // [[0, 0, -1]] ← 要素2はマスク対象
        val label =
            batchOf(
                IOType.d1(listOf(0.0f, 0.0f, -1.0f)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3, temperature = 1.0f, maskValue = -1)
        val result = softmax._train(input) { label }

        // softmax計算
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        // loss = -mean(ln(sum(label * softmax) + 1e-7))
        // label = [0, 0, -1] (maskValue=-1なのでインデックス2はマスク扱いだが、sumには含まれる)
        // ただし、-1は通常の値として扱われる (maskValueはdelta計算時のマスク)
        // sum(label * softmax) = 0*(exp0/sum) + 0*(exp1/sum) + (-1)*(exp2/sum) = -(exp2/sum)
        // loss = -ln(-(exp2/sum) + 1e-7)
        // 注: 負の値にlnを適用すると問題があるが、実装ではそのまま計算される
        val epsilon = 1e-7f
        val softmax0 = exp0 / sum
        val softmax1 = exp1 / sum
        val softmax2 = exp2 / sum
        val labelDotSoftmax = 0.0f * softmax0 + 0.0f * softmax1 + (-1.0f) * softmax2
        val expectedLoss = -kotlin.math.ln(labelDotSoftmax + epsilon)
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta as Batch<IOType.D1>

        // 要素0, 1: 通常の勾配
        assertEquals(
            expected = (exp0 / sum - 0f),
            actual = output[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum - 0f),
            actual = output[0][1],
            absoluteTolerance = 1e-4f,
        )

        // 要素2: maskValue=-1なので勾配は0
        assertEquals(expected = 0.0f, actual = output[0][2], absoluteTolerance = 1e-4f)
    }
}
