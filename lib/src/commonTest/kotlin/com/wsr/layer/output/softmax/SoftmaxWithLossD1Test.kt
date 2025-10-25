@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.softmax

import com.wsr.IOType
import com.wsr.layer.output.softmax.SoftmaxWithLossD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxWithLossD1Test {
    @Test
    fun `SoftmaxWithLossD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3)
        val result = softmax._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `SoftmaxWithLossD1の_train=softmax適用後にラベルを引いた値を返す`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )
        // [[0, 0, 1]]
        val label =
            listOf(
                IOType.d1(listOf(0.0, 0.0, 1.0)),
            )
        val softmax = SoftmaxWithLossD1(outputSize = 3)
        val result = softmax._train(input, label)

        // max = 3
        // exp(1-3) = exp(-2)
        // exp(2-3) = exp(-1)
        // exp(3-3) = exp(0) = 1
        val exp0 = exp(1.0 - 3.0)
        val exp1 = exp(2.0 - 3.0)
        val exp2 = exp(3.0 - 3.0)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        // [exp0/sum - 0, exp1/sum - 0, exp2/sum - 1]
        val output = result[0] as IOType.D1
        assertEquals(expected = exp0 / sum - 0.0, actual = output[0], absoluteTolerance = 1e-4)
        assertEquals(expected = exp1 / sum - 0.0, actual = output[1], absoluteTolerance = 1e-4)
        assertEquals(expected = exp2 / sum - 1.0, actual = output[2], absoluteTolerance = 1e-4)
    }
}
