@file:Suppress("NonAsciiCharacters")

package com.wsr.dot.inner

import com.wsr.IOType
import com.wsr.d1
import kotlin.test.Test
import kotlin.test.assertEquals

class D1ExtTest {
    @Test
    fun `D1·D1=内積`() {
        val a = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val b = IOType.d1(listOf(4.0f, 5.0f, 6.0f))
        val result = a inner b
        assertEquals(
            expected = 32.0f,
            actual = result,
        )
    }
}
