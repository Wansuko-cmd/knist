@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.debug

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.layer.Context
import com.wsr.layer.process.debug.DebugD3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.wsr.get
class DebugD3Test {
    @Test
    fun `DebugD3の_expect=入力をそのまま返し、onInputを呼び出す`() {
        var inputCaptured: Batch<IOType.D3>? = null
        val debug = DebugD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        ).apply {
            onInput = { inputCaptured = it }
            onDelta = {}
        }

        val input = batchOf(IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
        )
        val context = Context(input)

        val result = debug._expect(input, context) as Batch<IOType.D3>
        // 入力がそのまま返される
        assertEquals(expected = input, actual = result)

        // onInputが呼ばれている
        assertTrue(inputCaptured != null)
        assertEquals(expected = input, actual = inputCaptured)
    }

    @Test
    fun `DebugD3の_train=deltaをそのまま返し、onInputとonDeltaを呼び出す`() {
        var inputCaptured: Batch<IOType.D3>? = null
        var deltaCaptured: Batch<IOType.D3>? = null
        val debug = DebugD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        ).apply {
            onInput = { inputCaptured = it }
            onDelta = { deltaCaptured = it }
        }

        val input = batchOf(IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
        )
        val context = Context(input)

        val expectedDelta = batchOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0f },
        )

        val calcDelta: (Batch<IOType>) -> Batch<IOType> = { expectedDelta }

        val result = debug._train(input, context, calcDelta) as Batch<IOType.D3>
        // deltaがそのまま返される
        assertEquals(expected = expectedDelta, actual = result)

        // onInputが呼ばれている
        assertTrue(inputCaptured != null)
        assertEquals(expected = input, actual = inputCaptured)

        // onDeltaが呼ばれている
        assertTrue(deltaCaptured != null)
        assertEquals(expected = expectedDelta, actual = deltaCaptured)
    }
}
