@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.softmax

import com.wsr.IOType
import com.wsr.layer.process.function.softmax.SoftmaxD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxD1Test {
    @Test
    fun `SoftmaxD1の_expect=確率分布を計算する`() {
        val softmax = SoftmaxD1(outputSize = 3)

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )

        val result = softmax._expect(input)

        // max = 3
        val exp0 = exp(1.0f - 3.0f)
        val exp1 = exp(2.0f - 3.0f)
        val exp2 = exp(3.0f - 3.0f)
        val sum = exp0 + exp1 + exp2

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(
            expected = (exp0 / sum),
            actual = output[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp1 / sum),
            actual = output[1],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = (exp2 / sum),
            actual = output[2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `SoftmaxD1の_train=Softmaxの微分を正しく計算する`() {
        val softmax = SoftmaxD1(outputSize = 2)

        // [[1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f)),
            )

        // deltaは[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0f, 1.0f)))
        }

        val result = softmax._train(input, calcDelta)

        // output = softmax(input)
        val exp0 = exp(1.0f - 2.0f)
        val exp1 = exp(2.0f - 2.0f)
        val sum = exp0 + exp1
        val out0 = exp0 / sum
        val out1 = exp1 / sum

        // delta * output * (1 - output)
        val expected0 = 1.0f * out0 * (1 - out0)
        val expected1 = 1.0f * out1 * (1 - out1)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        assertEquals(
            expected = expected0,
            actual = dx[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = expected1,
            actual = dx[1],
            absoluteTolerance = 1e-4f,
        )
    }
}
