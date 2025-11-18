@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.sigmoid

import com.wsr.IOType
import com.wsr.output.sigmoid.SigmoidWithLossD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidWithLossD1Test {
    @Test
    fun `SigmoidWithLossD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val sigmoid = SigmoidWithLossD1(outputSize = 3)
        val result = sigmoid._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `SigmoidWithLossD1の_train=sigmoid適用後にラベルを引いた値を返す`() {
        // [[0, 1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(0.0f, 1.0f, 2.0f)),
            )
        // [[1, 0, 0]]
        val label =
            listOf(
                IOType.d1(listOf(1.0f, 0.0f, 0.0f)),
            )
        val sigmoid = SigmoidWithLossD1(outputSize = 3)
        val result = sigmoid._train(input, label)

        // sigmoid(0) = 1/(1+e^0) = 0.5f
        val sig0 = 1 / (1 + exp(-0.0f))
        // sigmoid(1) = 1/(1+e^-1) ≈ 0.7311f
        val sig1 = 1 / (1 + exp(-1.0f))
        // sigmoid(2) = 1/(1+e^-2) ≈ 0.8808f
        val sig2 = 1 / (1 + exp(-2.0f))

        assertEquals(expected = 1, actual = result.size)
        // [0.5f-1, 0.7311f-0, 0.8808f-0]
        val output = result[0] as IOType.D1
        assertEquals(expected = sig0 - 1.0f, actual = output[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig1 - 0.0f, actual = output[1], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig2 - 0.0f, actual = output[2], absoluteTolerance = 1e-4f)
    }
}
