@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.layer.Context
import com.wsr.layer.process.function.relu.SwishD1
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

import com.wsr.get

import com.wsr.Batch
import com.wsr.batchOf

class SwishD1Test {
    @Test
    fun `SwishD1の_expect=swish関数を適用`() {
        val swish = SwishD1(outputSize = 3)

        // [[0, 1, 2]]
        val input =
            batchOf(IOType.d1(listOf(0.0f, 1.0f, 2.0f)),
            )
        val context = Context(input)

        val result = swish._expect(input, context) as Batch<IOType.D1>
        // swish(x) = x / (1 + e^-x) = x * sigmoid(x)
        val sig0 = 1 / (1 + exp(-0.0f))
        val sig1 = 1 / (1 + exp(-1.0f))
        val sig2 = 1 / (1 + exp(-2.0f))

        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 0.0f * sig0, actual = output[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f * sig1, actual = output[1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 2.0f * sig2, actual = output[2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `SwishD1の_train=swish微分を適用`() {
        val swish = SwishD1(outputSize = 2)

        // [[0, 1]]
        val input =
            batchOf(IOType.d1(listOf(0.0f, 1.0f)),
            )
        val context = Context(input)

        // deltaは[1, 1]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(1.0f, 1.0f)))
        }

        val result = swish._train(input, context, calcDelta) as Batch<IOType.D1>
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
        assertEquals(
            expected = expected0,
            actual = dx[0],
            absoluteTolerance = 1e-4f,
        )
        assertEquals(
            expected = expected1,
            actual = dx[1],
            absoluteTolerance = 1e-4f,
        )
    }
}
