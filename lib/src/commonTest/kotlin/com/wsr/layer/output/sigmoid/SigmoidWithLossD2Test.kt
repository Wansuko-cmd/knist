@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.sigmoid

import com.wsr.IOType
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidWithLossD2Test {
    @Test
    fun `SigmoidWithLossD2の_expect=入力をそのまま返す`() {
        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val sigmoid = SigmoidWithLossD2(outputX = 2, outputY = 2)
        val result = sigmoid._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `SigmoidWithLossD2の_train=sigmoid適用後にラベルを引いた値を返す`() {
        // [[0, 1], [2, 3]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y).toFloat() },
            )
        // [[1, 0], [0, 1]]
        val label =
            listOf(
                IOType.d2(2, 2) { x, y -> if (x == y) 1.0 else 0.0 },
            )
        val sigmoid = SigmoidWithLossD2(outputX = 2, outputY = 2)
        val result = sigmoid._train(input, label)

        // sigmoid(0) = 1/(1+e^0) = 0.5
        val sig0 = 1 / (1 + exp(-0.0))
        // sigmoid(1) = 1/(1+e^-1) ≈ 0.7311
        val sig1 = 1 / (1 + exp(-1.0))
        // sigmoid(2) = 1/(1+e^-2) ≈ 0.8808
        val sig2 = 1 / (1 + exp(-2.0))
        // sigmoid(3) = 1/(1+e^-3) ≈ 0.9526
        val sig3 = 1 / (1 + exp(-3.0))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])

        // output = [[sigmoid(0)-1, sigmoid(1)-0], [sigmoid(2)-0, sigmoid(3)-1]]
        //        = [[0.5-1, 0.7311-0], [0.8808-0, 0.9526-1]]
        //        = [[-0.5, 0.7311], [0.8808, -0.0474]]
        assertEquals(expected = sig0 - 1.0, actual = output[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig1 - 0.0, actual = output[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = sig2 - 0.0, actual = output[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig3 - 1.0, actual = output[1, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `SigmoidWithLossD2の_train=複数バッチでも正しく動作する`() {
        // バッチ1: [[0, 1]]
        // バッチ2: [[2, 3]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> y.toFloat() },
                IOType.d2(1, 2) { _, y -> (y + 2).toFloat() },
            )
        // バッチ1のラベル: [[1, 0]]
        // バッチ2のラベル: [[0, 1]]
        val label =
            listOf(
                IOType.d2(1, 2) { _, y -> if (y == 0) 1.0 else 0.0 },
                IOType.d2(1, 2) { _, y -> if (y == 1) 1.0 else 0.0 },
            )
        val sigmoid = SigmoidWithLossD2(outputX = 1, outputY = 2)
        val result = sigmoid._train(input, label)

        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))
        val sig2 = 1 / (1 + exp(-2.0))
        val sig3 = 1 / (1 + exp(-3.0))

        assertEquals(expected = 2, actual = result.size)

        // バッチ1の出力: [[sigmoid(0)-1, sigmoid(1)-0]]
        val output1 = result[0] as IOType.D2
        assertEquals(expected = sig0 - 1.0, actual = output1[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig1 - 0.0, actual = output1[0, 1], absoluteTolerance = 1e-4)

        // バッチ2の出力: [[sigmoid(2)-0, sigmoid(3)-1]]
        val output2 = result[1] as IOType.D2
        assertEquals(expected = sig2 - 0.0, actual = output2[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig3 - 1.0, actual = output2[0, 1], absoluteTolerance = 1e-4)
    }
}
