@file:Suppress("NonAsciiCharacters")

package com.wsr.process.debug

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class DebugD2Test {
    @Test
    fun `DebugD2の_expect=入力をそのまま返し、onInputを呼び出す`() {
        var capturedInput: List<IOType.D2>? = null
        val debug =
            DebugD2(
                outputX = 2,
                outputY = 2,
                onInput = { capturedInput = it },
                onDelta = {},
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        val result = debug._expect(input)

        // 入力をそのまま返す
        assertEquals(expected = input, actual = result)
        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
    }

    @Test
    fun `DebugD2の_train=deltaをそのまま返し、onInputとonDeltaを呼び出す`() {
        var capturedInput: List<IOType.D2>? = null
        var capturedDelta: List<IOType.D2>? = null
        val debug =
            DebugD2(
                outputX = 2,
                outputY = 2,
                onInput = { capturedInput = it },
                onDelta = { capturedDelta = it },
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0 })
        }

        val result = debug._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        // deltaは[[2, 4], [6, 8]]
        assertEquals(expected = 2.0, actual = dx[0, 0])
        assertEquals(expected = 4.0, actual = dx[0, 1])
        assertEquals(expected = 6.0, actual = dx[1, 0])
        assertEquals(expected = 8.0, actual = dx[1, 1])

        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
        // onDeltaが呼び出される
        assertEquals(expected = 2.0, actual = (capturedDelta!![0] as IOType.D2)[0, 0])
        assertEquals(expected = 4.0, actual = (capturedDelta!![0] as IOType.D2)[0, 1])
        assertEquals(expected = 6.0, actual = (capturedDelta!![0] as IOType.D2)[1, 0])
        assertEquals(expected = 8.0, actual = (capturedDelta!![0] as IOType.D2)[1, 1])
    }
}
