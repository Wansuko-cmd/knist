@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class D1ExtTest {
    @Test
    fun `List_D1のtoD2=D1をまとめたD2`() {
        val list =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
                IOType.d1(listOf(4.0, 5.0, 6.0)),
                IOType.d1(listOf(7.0, 8.0, 9.0)),
            )
        val result = list.toD2()
        assertEquals(
            expected = IOType.d2(3, 3) { x, y -> (x * 3 + y + 1).toDouble() },
            actual = result,
        )
    }
}
