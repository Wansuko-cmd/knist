@file:Suppress("NonAsciiCharacters")

package com.wsr.operator

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class DivExtTest {
    @Test
    fun `D1÷Float=各要素をFloatで割ったD1`() {
        val a = IOType.d1(listOf(2.0f, 4.0f, 6.0f))
        val result = a / 2.0f
        assertEquals(
            expected = IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            actual = result,
        )
    }

    @Test
    fun `Float÷D2=Floatを各要素で割ったD2`() {
        val a = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result = 12.0f / a
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> 12.0f / (x * 2 + y + 1) },
            actual = result,
        )
    }

    @Test
    fun `Float÷D3=Floatを各要素で割ったD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = 24.0f / a
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> 24.0f / (x * 4 + y * 2 + z + 1) },
            actual = result,
        )
    }
}
