@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.sigmoid

import com.wsr.IOType
import com.wsr.layer.process.function.sigmoid.SigmoidD2
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidD2Test {
    @Test
    fun `SigmoidD2の_expect=sigmoid関数を適用`() {
        val sigmoid = SigmoidD2(outputX = 2, outputY = 2)

        // [[0, 1], [2, 3]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y).toDouble() },
            )

        val result = sigmoid._expect(input)

        // sigmoid(x) = 1 / (1 + e^-x)
        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = sig0, actual = output[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = sig1, actual = output[0, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `SigmoidD2の_train=sigmoid微分を適用`() {
        val sigmoid = SigmoidD2(outputX = 1, outputY = 2)

        // [[0, 1]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> y.toDouble() },
            )

        // deltaは[[1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(1, 2) { _, _ -> 1.0 })
        }

        val result = sigmoid._train(input, calcDelta)

        // output = sigmoid(input)
        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))

        // delta * output * (1 - output)
        val expected0 = 1.0 * sig0 * (1 - sig0)
        val expected1 = 1.0 * sig1 * (1 - sig1)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = expected0, actual = dx[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = expected1, actual = dx[0, 1], absoluteTolerance = 1e-4)
    }
}
