@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class D1Test {
    @Test
    fun `D1のget=各要素取得`() {
        val d1 = IOType.d1(listOf(1.0, 2.0, 3.0, 4.0))
        assertEquals(expected = 1.0, actual = d1[0])
        assertEquals(expected = 2.0, actual = d1[1])
        assertEquals(expected = 3.0, actual = d1[2])
        assertEquals(expected = 4.0, actual = d1[3])
    }

    @Test
    fun `D1のset=各要素設定`() {
        val d1 = IOType.d1(listOf(1.0, 2.0, 3.0, 4.0))
        d1[0] = 4.0
        d1[1] = 3.0
        d1[2] = 2.0
        d1[3] = 1.0
        assertEquals(expected = 4.0, actual = d1[0])
        assertEquals(expected = 3.0, actual = d1[1])
        assertEquals(expected = 2.0, actual = d1[2])
        assertEquals(expected = 1.0, actual = d1[3])
    }
}
