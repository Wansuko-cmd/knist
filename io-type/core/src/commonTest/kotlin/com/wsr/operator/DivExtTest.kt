@file:Suppress("NonAsciiCharacters")

package com.wsr.operator

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class DivExtTest {
    @Test
    fun `D1÷Double=各要素をDoubleで割ったD1`() {
        val a = IOType.d1(listOf(2.0, 4.0, 6.0))
        val result = a / 2.0
        assertEquals(
            expected = IOType.d1(listOf(1.0, 2.0, 3.0)),
            actual = result,
        )
    }

    @Test
    fun `Double÷D2=Doubleを各要素で割ったD2`() {
        val a = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result = 12.0 / a
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> 12.0 / (x * 2 + y + 1) },
            actual = result,
        )
    }

    @Test
    fun `Double÷D3=Doubleを各要素で割ったD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }
        val result = 24.0 / a
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> 24.0 / (x * 4 + y * 2 + z + 1) },
            actual = result,
        )
    }
}
