@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.debug

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batchOf
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.process.debug.DebugD2
import kotlin.test.Test
import kotlin.test.assertEquals

class DebugD2Test {
    @Test
    fun `DebugD2の_expect=入力をそのまま返し、onInputを呼び出す`() {
        var capturedInput: Batch<IOType.D2>? = null
        val debug =
            DebugD2(
                outputX = 2,
                outputY = 2,
            ).apply {
                onInput = { capturedInput = it }
                onDelta = {}
            }

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        val result = debug._expect(input, context) as Batch<IOType.D2>
        // 入力をそのまま返す
        assertEquals(expected = input, actual = result)
        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
    }

    @Test
    fun `DebugD2の_train=deltaをそのまま返し、onInputとonDeltaを呼び出す`() {
        var capturedInput: Batch<IOType.D2>? = null
        var capturedDelta: Batch<IOType.D2>? = null
        val debug =
            DebugD2(
                outputX = 2,
                outputY = 2,
            ).apply {
                onInput = { capturedInput = it }
                onDelta = { capturedDelta = it }
            }

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        val result = debug._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // deltaは[[2, 4], [6, 8]]
        assertEquals(expected = 2.0f, actual = dx[0, 0])
        assertEquals(expected = 4.0f, actual = dx[0, 1])
        assertEquals(expected = 6.0f, actual = dx[1, 0])
        assertEquals(expected = 8.0f, actual = dx[1, 1])

        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
        // onDeltaが呼び出される
        assertEquals(expected = 2.0f, actual = capturedDelta!![0][0, 0])
        assertEquals(expected = 4.0f, actual = capturedDelta[0][0, 1])
        assertEquals(expected = 6.0f, actual = capturedDelta[0][1, 0])
        assertEquals(expected = 8.0f, actual = capturedDelta[0][1, 1])
    }
}
