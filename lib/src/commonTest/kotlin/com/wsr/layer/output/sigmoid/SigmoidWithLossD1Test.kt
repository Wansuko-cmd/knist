@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.sigmoid

import com.wsr.IOType
import com.wsr.layer.output.sigmoid.SigmoidWithLossD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidWithLossD1Test {
    @Test
    fun `SigmoidWithLossD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
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
                IOType.d1(listOf(0.0, 1.0, 2.0)),
            )
        // [[1, 0, 0]]
        val label =
            listOf(
                IOType.d1(listOf(1.0, 0.0, 0.0)),
            )
        val sigmoid = SigmoidWithLossD1(outputSize = 3)
        val result = sigmoid._train(input, label)

        // sigmoid(0) = 1/(1+e^0) = 0.5
        val sig0 = 1 / (1 + exp(-0.0))
        // sigmoid(1) = 1/(1+e^-1) ≈ 0.7311
        val sig1 = 1 / (1 + exp(-1.0))
        // sigmoid(2) = 1/(1+e^-2) ≈ 0.8808
        val sig2 = 1 / (1 + exp(-2.0))

        assertEquals(expected = 1, actual = result.size)
        // [0.5-1, 0.7311-0, 0.8808-0]
        val output = result[0] as IOType.D1
        assertEquals(expected = sig0 - 1.0, actual = output[0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig1 - 0.0, actual = output[1], absoluteTolerance = 1e-4)
        assertEquals(expected = sig2 - 0.0, actual = output[2], absoluteTolerance = 1e-4)
    }
}
