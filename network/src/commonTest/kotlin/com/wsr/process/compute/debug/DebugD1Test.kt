@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.debug

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.process.Context
import kotlin.test.Test
import kotlin.test.assertEquals
class DebugD1Test {
    @Test
    fun `DebugD1の_expect=入力をそのまま返し、onInputを呼び出す`() {
        var capturedInput: Batch<IOType.D1>? = null
        val debug =
            DebugD1(outputSize = 3).apply {
                onInput = { capturedInput = it }
                onDelta = {}
            }

        // [1, 2, 3]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        val result = debug._expect(input, context) as Batch<IOType.D1>
        // 入力をそのまま返す
        assertEquals(expected = input, actual = result)
        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
    }

    @Test
    fun `DebugD1の_train=deltaをそのまま返し、onInputとonDeltaを呼び出す`() {
        var capturedInput: Batch<IOType.D1>? = null
        var capturedDelta: Batch<IOType.D1>? = null
        val debug =
            DebugD1(outputSize = 3).apply {
                onInput = { capturedInput = it }
                onDelta = { capturedDelta = it }
            }

        // [1, 2, 3]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        // deltaは[2, 4, 6]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f)))
        }

        val result = debug._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // deltaは[2, 4, 6]
        assertEquals(expected = 2.0f, actual = dx[0])
        assertEquals(expected = 4.0f, actual = dx[1])
        assertEquals(expected = 6.0f, actual = dx[2])

        // onInputが呼び出される
        assertEquals(expected = input, actual = capturedInput)
        // onDeltaが呼び出される
        assertEquals(expected = 2.0f, actual = capturedDelta!![0][0])
        assertEquals(expected = 4.0f, actual = capturedDelta[0][1])
        assertEquals(expected = 6.0f, actual = capturedDelta[0][2])
    }
}
