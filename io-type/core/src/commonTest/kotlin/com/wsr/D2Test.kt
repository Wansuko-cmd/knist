@file:Suppress("NonAsciiCharacters")

package com.wsr

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class D2Test {
    @Test
    fun `D2のget_ij=各要素取得`() {
        val d2 = IOType.d2(listOf(2, 2)) { x, y -> x + y * 2.0f }
        assertEquals(expected = 0.0f, actual = d2[0, 0])
        assertEquals(expected = 2.0f, actual = d2[0, 1])
        assertEquals(expected = 1.0f, actual = d2[1, 0])
        assertEquals(expected = 3.0f, actual = d2[1, 1])
    }

    @Test
    fun `D2のget_i=各D1取得`() {
        val d2 = IOType.d2(listOf(2, 2)) { x, y -> x + y * 2.0f }
        assertEquals(expected = IOType.d1(value = listOf(0.0f, 2.0f)), actual = d2[0])
        assertEquals(expected = IOType.d1(value = listOf(1.0f, 3.0f)), actual = d2[1])
    }

    @Test
    fun `D2のset=各要素設定`() {
        val d2 = IOType.d2(listOf(2, 2)) { x, y -> x + y * 2.0f }
        d2[0, 0] = 3.0f
        d2[0, 1] = 1.0f
        d2[1, 0] = 2.0f
        d2[1, 1] = 0.0f
        assertEquals(expected = 3.0f, actual = d2[0, 0])
        assertEquals(expected = 1.0f, actual = d2[0, 1])
        assertEquals(expected = 2.0f, actual = d2[1, 0])
        assertEquals(expected = 0.0f, actual = d2[1, 1])
    }
}
