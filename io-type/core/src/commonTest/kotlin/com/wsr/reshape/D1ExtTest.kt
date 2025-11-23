@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import kotlin.test.Test
import kotlin.test.assertEquals

class D1ExtTest {
    @Test
    fun `List_D1のtoD2=D1をまとめたD2`() {
        val list =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
                IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
                IOType.d1(listOf(7.0f, 8.0f, 9.0f)),
            )
        val result = list.toD2()
        assertEquals(
            expected = IOType.d2(3, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            actual = result,
        )
    }
}
