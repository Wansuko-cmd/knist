@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.function.relu

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.layer.Context
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
            batchOf(
                IOType.d2(1, 3) { _, y -> y.toFloat() },
            )
        val context = Context(input)

        val result = swish._expect(input, context) as Batch<IOType.D2>
        // swish(x) = x / (1 + e^-x) = x * sigmoid(x)
        val sig0 = 1 / (1 + exp(-0.0f))
        val sig1 = 1 / (1 + exp(-1.0f))
        val sig2 = 1 / (1 + exp(-2.0f))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 0.0f * sig0, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f * sig1, actual = output[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 2.0f * sig2, actual = output[0, 2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SwishD2の_train=swish微分を適用`() {
        val swish = SwishD2(outputX = 1, outputY = 2)

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

        val result = swish._train(input, context, calcDelta) as Batch<IOType.D2>
        // sigmoid = 1 / (1 + e^-x)
        val sig0 = 1 / (1 + exp(-0.0f))
        val sig1 = 1 / (1 + exp(-1.0f))
        // output = x * sigmoid(x)
        val out0 = 0.0f * sig0
        val out1 = 1.0f * sig1

        // dx = (output + sigmoid * (1 - output)) * delta
        val expected0 = (out0 + sig0 * (1 - out0)) * 1.0f
        val expected1 = (out1 + sig1 * (1 - out1)) * 1.0f

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = expected0, actual = dx[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = expected1, actual = dx[0, 1], absoluteTolerance = 1e-4f)
    }
}
