@file:Suppress("NonAsciiCharacters")

package com.wsr

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class D1Test {
    @Test
    fun `D1のget=各要素取得`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f))
        assertEquals(expected = 1.0f, actual = d1[0])
        assertEquals(expected = 2.0f, actual = d1[1])
        assertEquals(expected = 3.0f, actual = d1[2])
        assertEquals(expected = 4.0f, actual = d1[3])
    }

    @Test
    fun `D1のset=各要素設定`() {
        val d1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f))
        d1[0] = 4.0f
        d1[1] = 3.0f
        d1[2] = 2.0f
        d1[3] = 1.0f
        assertEquals(expected = 4.0f, actual = d1[0])
        assertEquals(expected = 3.0f, actual = d1[1])
        assertEquals(expected = 2.0f, actual = d1[2])
        assertEquals(expected = 1.0f, actual = d1[3])
    }
}
