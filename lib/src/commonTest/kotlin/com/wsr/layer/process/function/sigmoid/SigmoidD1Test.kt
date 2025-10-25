@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.sigmoid

import com.wsr.IOType
import com.wsr.layer.process.function.sigmoid.SigmoidD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidD1Test {
    @Test
    fun `SigmoidD1の_expect=sigmoid関数を適用`() {
        val sigmoid = SigmoidD1(outputSize = 3)

        // [[0, 1, 2]]
        val input =
            listOf(
                IOType.d1(listOf(0.0, 1.0, 2.0)),
            )

        val result = sigmoid._expect(input)

        // sigmoid(x) = 1 / (1 + e^-x)
        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))
        val sig2 = 1 / (1 + exp(-2.0))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = sig0, actual = output[0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig1, actual = output[1], absoluteTolerance = 1e-4)
        assertEquals(expected = sig2, actual = output[2], absoluteTolerance = 1e-4)
    }

    @Test
    fun `SigmoidD1の_train=sigmoid微分を適用`() {
        val sigmoid = SigmoidD1(outputSize = 2)

        // [[0, 1]]
        val input =
            listOf(
                IOType.d1(listOf(0.0, 1.0)),
            )

        // deltaは[1, 1]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(1.0, 1.0)))
        }

        val result = sigmoid._train(input, calcDelta)

        // output = sigmoid(input)
        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))

        // delta * output * (1 - output)
        val expected0 = 1.0 * sig0 * (1 - sig0)
        val expected1 = 1.0 * sig1 * (1 - sig1)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        assertEquals(expected = expected0, actual = dx[0], absoluteTolerance = 1e-4)
        assertEquals(expected = expected1, actual = dx[1], absoluteTolerance = 1e-4)
    }
}
