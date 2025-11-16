@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.layer.process.function.relu.SwishD2
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SwishD2Test {
    @Test
    fun `SwishD2の_expect=swish関数を適用`() {
        val swish = SwishD2(outputX = 1, outputY = 3)

        // [[0, 1, 2]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> y.toFloat() },
            )

        val result = swish._expect(input)

        // swish(x) = x / (1 + e^-x) = x * sigmoid(x)
        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))
        val sig2 = 1 / (1 + exp(-2.0))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 0.0 * sig0, actual = output[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 * sig1, actual = output[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 2.0 * sig2, actual = output[0, 2], absoluteTolerance = 1e-4)
    }

    @Test
    fun `SwishD2の_train=swish微分を適用`() {
        val swish = SwishD2(outputX = 1, outputY = 2)

        // [[0, 1]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> y.toFloat() },
            )

        // deltaは[[1, 1]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(1, 2) { _, _ -> 1.0 })
        }

        val result = swish._train(input, calcDelta)

        // sigmoid = 1 / (1 + e^-x)
        val sig0 = 1 / (1 + exp(-0.0))
        val sig1 = 1 / (1 + exp(-1.0))
        // output = x * sigmoid(x)
        val out0 = 0.0 * sig0
        val out1 = 1.0 * sig1

        // dx = (output + sigmoid * (1 - output)) * delta
        val expected0 = (out0 + sig0 * (1 - out0)) * 1.0
        val expected1 = (out1 + sig1 * (1 - out1)) * 1.0

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = expected0, actual = dx[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = expected1, actual = dx[0, 1], absoluteTolerance = 1e-4)
    }
}
