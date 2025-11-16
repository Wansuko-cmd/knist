@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.debug

import com.wsr.IOType
import com.wsr.layer.process.debug.DebugD1
import kotlin.test.Test
import kotlin.test.assertEquals

class DebugD1Test {
    @Test
    fun `DebugD1の_expect=入力をそのまま返し、onInputを呼び出す`() {
        var capturedInput: List<IOType.D1>? = null
        val debug =
            DebugD1(outputSize = 3).apply {
                onInput = { capturedInput = it }
                onDelta = {}
            }

        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )

        val result = debug._expect(input)

        // 入力をそのまま返す
        assertEquals(expected = input, actual = result)
        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
    }

    @Test
    fun `DebugD1の_train=deltaをそのまま返し、onInputとonDeltaを呼び出す`() {
        var capturedInput: List<IOType.D1>? = null
        var capturedDelta: List<IOType.D1>? = null
        val debug =
            DebugD1(outputSize = 3).apply {
                onInput = { capturedInput = it }
                onDelta = { capturedDelta = it }
            }

        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )

        // deltaは[2, 4, 6]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f)))
        }

        val result = debug._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        // deltaは[2, 4, 6]
        assertEquals(expected = 2.0f, actual = dx[0])
        assertEquals(expected = 4.0f, actual = dx[1])
        assertEquals(expected = 6.0f, actual = dx[2])

        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
        // onDeltaが呼び出される
        assertEquals(expected = 2.0f, actual = (capturedDelta!![0] as IOType.D1)[0])
        assertEquals(expected = 4.0f, actual = (capturedDelta!![0] as IOType.D1)[1])
        assertEquals(expected = 6.0f, actual = (capturedDelta!![0] as IOType.D1)[2])
    }
}
