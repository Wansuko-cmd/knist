@file:Suppress("NonAsciiCharacters")

package com.wsr.output.sigmoid

import com.wsr.IOType
import com.wsr.output.sigmoid.SigmoidWithLossD2
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

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
                IOType.d2(2, 2) { x, y -> if (x == y) 1.0f else 0.0f },
            )
        val sigmoid = SigmoidWithLossD2(outputX = 2, outputY = 2)
        val result = sigmoid._train(input, label)

        // sigmoid(0) = 1/(1+e^0) = 0.5f
        val sig0 = 1 / (1 + exp(-0.0f))
        // sigmoid(1) = 1/(1+e^-1) ≈ 0.7311f
        val sig1 = 1 / (1 + exp(-1.0f))
        // sigmoid(2) = 1/(1+e^-2) ≈ 0.8808f
        val sig2 = 1 / (1 + exp(-2.0f))
        // sigmoid(3) = 1/(1+e^-3) ≈ 0.9526f
        val sig3 = 1 / (1 + exp(-3.0f))

        // loss = -mean(sum(label * ln(sigmoid + 1e-7) + (1-label) * ln(1-sigmoid + 1e-7)))
        // sum() sums all elements in each batch, average() averages across batches
        // label = [[1, 0], [0, 1]], sigmoid = [[0.5, 0.7311], [0.8808, 0.9526]]
        // loss = -(sum([1*ln(0.5+ε)+0*ln(0.5+ε), 0*ln(0.7311+ε)+1*ln(0.2689+ε), 0*ln(0.8808+ε)+1*ln(0.1192+ε), 1*ln(0.9526+ε)+0*ln(0.0474+ε)])) / batchSize
        // loss = -(ln(0.5) + ln(0.2689) + ln(0.1192) + ln(0.9526)) / 1
        val epsilon = 1e-7f
        val loss00 = 1.0f * kotlin.math.ln(sig0 + epsilon) + (1.0f - 1.0f) * kotlin.math.ln(1.0f - sig0 + epsilon)
        val loss01 = 0.0f * kotlin.math.ln(sig1 + epsilon) + (1.0f - 0.0f) * kotlin.math.ln(1.0f - sig1 + epsilon)
        val loss10 = 0.0f * kotlin.math.ln(sig2 + epsilon) + (1.0f - 0.0f) * kotlin.math.ln(1.0f - sig2 + epsilon)
        val loss11 = 1.0f * kotlin.math.ln(sig3 + epsilon) + (1.0f - 1.0f) * kotlin.math.ln(1.0f - sig3 + epsilon)
        val expectedLoss = -(loss00 + loss01 + loss10 + loss11)
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])

        // output = [[sigmoid(0)-1, sigmoid(1)-0], [sigmoid(2)-0, sigmoid(3)-1]]
        //        = [[0.5f-1, 0.7311f-0], [0.8808f-0, 0.9526f-1]]
        //        = [[-0.5f, 0.7311f], [0.8808f, -0.0474f]]
        assertEquals(expected = sig0 - 1f, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig1 - 0f, actual = output[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig2 - 0f, actual = output[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig3 - 1f, actual = output[1, 1], absoluteTolerance = 1e-4f)
    }
}
