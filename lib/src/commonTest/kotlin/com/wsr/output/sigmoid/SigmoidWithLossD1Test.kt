@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.output.sigmoid

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.output.sigmoid.SigmoidWithLossD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class SigmoidWithLossD1Test {
    @Test
    fun `SigmoidWithLossD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            batchOf(
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
            batchOf(
                IOType.d1(listOf(0.0f, 1.0f, 2.0f)),
            )
        // [[1, 0, 0]]
        val label =
            batchOf(
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

        // loss = -mean(sum(label * ln(sigmoid + 1e-7) + (1-label) * ln(1-sigmoid + 1e-7)))
        // sum() sums all elements in each batch, average() averages across batches
        // label = [1, 0, 0], sigmoid = [0.5, 0.7311, 0.8808]
        // loss = -(sum([1*ln(0.5+ε) + 0*ln(0.5+ε), 0*ln(0.7311+ε) + 1*ln(0.2689+ε), 0*ln(0.8808+ε) + 1*ln(0.1192+ε)])) / batchSize
        // loss = -(ln(0.5) + ln(0.2689) + ln(0.1192)) / 1
        val epsilon = 1e-7f
        val loss0 = 1.0f * kotlin.math.ln(sig0 + epsilon) + (1.0f - 1.0f) * kotlin.math.ln(1.0f - sig0 + epsilon)
        val loss1 = 0.0f * kotlin.math.ln(sig1 + epsilon) + (1.0f - 0.0f) * kotlin.math.ln(1.0f - sig1 + epsilon)
        val loss2 = 0.0f * kotlin.math.ln(sig2 + epsilon) + (1.0f - 0.0f) * kotlin.math.ln(1.0f - sig2 + epsilon)
        val expectedLoss = -(loss0 + loss1 + loss2)
        assertEquals(expected = expectedLoss, actual = result.loss, absoluteTolerance = 1e-5f)

        assertEquals(expected = 1, actual = result.delta.size)
        // [0.5f-1, 0.7311f-0, 0.8808f-0]
        val output = result.delta as Batch<IOType.D1>
        assertEquals(expected = sig0 - 1.0f, actual = output[0][0], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig1 - 0.0f, actual = output[0][1], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig2 - 0.0f, actual = output[0][2], absoluteTolerance = 1e-4f)
    }
}
