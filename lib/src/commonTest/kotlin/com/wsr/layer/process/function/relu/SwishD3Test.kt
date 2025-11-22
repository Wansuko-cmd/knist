@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.function.relu

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.d3
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.function.relu.SwishD3
import com.wsr.set
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class SwishD3Test {
    @Test
    fun `SwishD3の_expect=swish関数を適用`() {
        val swish = SwishD3(outputX = 1, outputY = 1, outputZ = 3)

        // [[[0, 1, 2]]]
        val input =
            batchOf(
                IOType.d3(1, 1, 3) { _, _, z -> z.toFloat() },
            )
        val context = Context(input)

        val result = swish._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        // swish(x) = x * sigmoid(x) = x / (1 + exp(-x))
        // swish(0) = 0
        // swish(1) = 1 / (1 + exp(-1)) ≈ 0.731f
        // swish(2) = 2 / (1 + exp(-2)) ≈ 1.762f
        assertEquals(expected = 0.0f, actual = output[0, 0, 0], absoluteTolerance = 1e-3f)
        assertEquals(expected = 0.731f, actual = output[0, 0, 1], absoluteTolerance = 1e-3f)
        assertEquals(expected = 1.762f, actual = output[0, 0, 2], absoluteTolerance = 1e-3f)
    }

    @Test
    fun `SwishD3の_train=swish微分を適用`() {
        val swish = SwishD3(outputX = 1, outputY = 1, outputZ = 2)

        // [[[1, 2]]]
        val input =
            batchOf(
                IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() },
            )
        val context = Context(input)

        // 全て1のdelta
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(1, 1, 2) { _, _, _ -> 1.0f })
        }

        val result = swish._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // swish'(x) = sigmoid(x) + x * sigmoid(x) * (1 - sigmoid(x))
        // = swish(x) + sigmoid(x) * (1 - swish(x))
        val sigmoid1 = 1.0f / (1.0f + exp(-1.0f))
        val swish1 = 1.0f * sigmoid1
        val expected1 = swish1 + sigmoid1 * (1.0f - swish1)

        val sigmoid2 = 1.0f / (1.0f + exp(-2.0f))
        val swish2 = 2.0f * sigmoid2
        val expected2 = swish2 + sigmoid2 * (1.0f - swish2)

        assertEquals(expected = expected1, actual = dx[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = expected2, actual = dx[0, 0, 1], absoluteTolerance = 1e-6f)
    }
}
