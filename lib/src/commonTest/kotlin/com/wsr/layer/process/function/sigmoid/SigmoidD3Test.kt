@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.sigmoid

import com.wsr.IOType
import com.wsr.layer.process.function.sigmoid.SigmoidD3
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidD3Test {
    @Test
    fun `SigmoidD3の_expect=sigmoid関数を適用`() {
        val sigmoid = SigmoidD3(outputX = 1, outputY = 1, outputZ = 3)

        // [[[0, 1, 2]]]
        val input =
            listOf(
                IOType.d3(1, 1, 3) { _, _, z -> z.toFloat() },
            )

        val result = sigmoid._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        // sigmoid(x) = 1 / (1 + exp(-x))
        // sigmoid(0) = 0.5f
        // sigmoid(1) ≈ 0.731f
        // sigmoid(2) ≈ 0.881f
        assertEquals(expected = 0.5f, actual = output[0, 0, 0], absoluteTolerance = 1e-3f)
        assertEquals(expected = 0.731f, actual = output[0, 0, 1], absoluteTolerance = 1e-3f)
        assertEquals(expected = 0.881f, actual = output[0, 0, 2], absoluteTolerance = 1e-3f)
    }

    @Test
    fun `SigmoidD3の_train=sigmoid微分を適用`() {
        val sigmoid = SigmoidD3(outputX = 1, outputY = 1, outputZ = 2)

        // [[[1, 2]]]
        val input =
            listOf(
                IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(1, 1, 2) { _, _, _ -> 1.0f })
        }

        val result = sigmoid._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
        // sigmoid'(x) = sigmoid(x) * (1 - sigmoid(x))
        val sig1 = 1.0f / (1.0f + exp(-1.0f))
        val expected1 = sig1 * (1.0f - sig1)

        val sig2 = 1.0f / (1.0f + exp(-2.0f))
        val expected2 = sig2 * (1.0f - sig2)

        assertEquals(expected = expected1, actual = dx[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = expected2, actual = dx[0, 0, 1], absoluteTolerance = 1e-6f)
    }
}
