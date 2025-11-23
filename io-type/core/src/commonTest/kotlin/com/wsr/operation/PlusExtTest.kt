@file:Suppress("NonAsciiCharacters")

package com.wsr.operation

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import kotlin.test.Test
import kotlin.test.assertEquals

class PlusExtTest {
    @Test
    fun `D1+D1=対応するindexの値を足したD1`() {
        val a = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val b = IOType.d1(listOf(2.0f, 3.0f, 4.0f))
        assertEquals(
            expected = IOType.d1(listOf(3.0f, 5.0f, 7.0f)),
            actual = a + b,
        )
    }

    @Test
    fun `D2+D2=対応するindexの値を足したD2`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val b = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val result = a + b
        assertEquals(
            expected = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() * 2 },
            actual = result,
        )
    }

    @Test
    fun `D3+D3=対応するindexの値を足したD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val b = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a + b
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() * 2 },
            actual = result,
        )
    }
}
