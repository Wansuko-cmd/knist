@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.sigmoid

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
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
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y).toFloat() },
            )
        val context = Context(input)

        val result = sigmoid._expect(input, context) as Batch<IOType.D2>
        // sigmoid(x) = 1 / (1 + e^-x)
        val sig0 = 1 / (1 + exp(-0.0f))
        val sig1 = 1 / (1 + exp(-1.0f))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = sig0, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = sig1, actual = output[0, 1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SigmoidD2の_train=sigmoid微分を適用`() {
        val sigmoid = SigmoidD2(outputX = 1, outputY = 2)

        // [[0, 1]]
        val input =
            batchOf(
                IOType.d2(1, 2) { _, y -> y.toFloat() },
            )
        val context = Context(input)

        // deltaは[[1, 1]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(1, 2) { _, _ -> 1.0f })
        }

        val result = sigmoid._train(input, context, calcDelta) as Batch<IOType.D2>
        // output = sigmoid(input)
        val sig0 = 1 / (1 + exp(-0.0f))
        val sig1 = 1 / (1 + exp(-1.0f))

        // delta * output * (1 - output)
        val expected0 = 1.0f * sig0 * (1 - sig0)
        val expected1 = 1.0f * sig1 * (1 - sig1)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = expected0, actual = dx[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = expected1, actual = dx[0, 1], absoluteTolerance = 1e-4f)
    }
}
