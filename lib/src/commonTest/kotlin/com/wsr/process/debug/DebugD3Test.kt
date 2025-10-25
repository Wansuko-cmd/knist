@file:Suppress("NonAsciiCharacters")

package com.wsr.process.debug

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DebugD3Test {
    @Test
    fun `DebugD3の_expect=入力をそのまま返し、onInputを呼び出す`() {
        var inputCaptured: List<IOType.D3>? = null
        val debug = DebugD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        ).apply {
            onInput = { inputCaptured = it }
            onDelta = {}
        }

        val input = listOf(
            IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
        )

        val result = debug._expect(input)

        // 入力がそのまま返される
        assertEquals(expected = input, actual = result)

        // onInputが呼ばれている
        assertTrue(inputCaptured != null)
        assertEquals(expected = input, actual = inputCaptured)
    }

    @Test
    fun `DebugD3の_train=deltaをそのまま返し、onInputとonDeltaを呼び出す`() {
        var inputCaptured: List<IOType.D3>? = null
        var deltaCaptured: List<IOType.D3>? = null
        val debug = DebugD3(
            outputX = 2,
            outputY = 2,
            outputZ = 2,
        ).apply {
            onInput = { inputCaptured = it }
            onDelta = { deltaCaptured = it }
        }

        val input = listOf(
            IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
        )

        val expectedDelta = listOf(
            IOType.d3(2, 2, 2) { _, _, _ -> 1.0 },
        )

        val calcDelta: (List<IOType>) -> List<IOType> = { expectedDelta }

        val result = debug._train(input, calcDelta)

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
