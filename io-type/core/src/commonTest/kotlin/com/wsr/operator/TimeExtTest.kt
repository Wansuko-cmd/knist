@file:Suppress("NonAsciiCharacters")

package com.wsr.operator

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeExtTest {
    @Test
    fun `Float×D1=各要素にFloatをかけたD1`() {
        val a = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val result = 2.0f * a
        assertEquals(
            expected = IOType.d1(listOf(2.0f, 4.0f, 6.0f)),
            actual = result,
        )
    }

    @Test
    fun `Float×D2=各要素にFloatをかけたD2`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val result = 2.0f * a
        assertEquals(
            expected = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() * 2 },
            actual = result,
        )
    }

    @Test
    fun `Float×D3=各要素にFloatをかけたD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = 2.0f * a
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() * 2 },
            actual = result,
        )
    }
}
