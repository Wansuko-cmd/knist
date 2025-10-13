@file:Suppress("NonAsciiCharacters")

package com.wsr.dot.inner

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class D1ExtTest {
    @Test
    fun `D1·D1=内積`() {
        val a = IOType.d1(listOf(1.0, 2.0, 3.0))
        val b = IOType.d1(listOf(4.0, 5.0, 6.0))
        val result = a inner b
        assertEquals(
            expected = 32.0,
            actual = result,
        )
    }

    @Test
    fun `D1·List_D1=各D1との内積のリスト`() {
        val a = IOType.d1(listOf(1.0, 2.0, 3.0))
        val list =
            listOf(
                IOType.d1(listOf(1.0, 0.0, 0.0)),
                IOType.d1(listOf(0.0, 1.0, 0.0)),
                IOType.d1(listOf(0.0, 0.0, 1.0)),
            )
        val result = a inner list
        assertEquals(
            expected = listOf(1.0, 2.0, 3.0),
            actual = result,
        )
    }

    @Test
    fun `List_D1·List_D1=各要素同士の内積のリスト`() {
        val list1 =
            listOf(
                IOType.d1(listOf(1.0, 2.0)),
                IOType.d1(listOf(3.0, 4.0)),
            )
        val list2 =
            listOf(
                IOType.d1(listOf(2.0, 3.0)),
                IOType.d1(listOf(4.0, 5.0)),
            )
        // 1*2 + 2*3 = 8
        // 3*4 + 4*5 = 32
        val result = list1 inner list2
        assertEquals(
            expected = listOf(8.0, 32.0),
            actual = result,
        )
    }
}
