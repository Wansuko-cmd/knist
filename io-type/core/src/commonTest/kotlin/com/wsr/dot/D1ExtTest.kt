@file:Suppress("NonAsciiCharacters")

package com.wsr.dot

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class D1ExtTest {
    @Test
    fun `D1·D1=内積`() {
        val a = IOType.d1(listOf(1.0, 2.0, 3.0))
        val b = IOType.d1(listOf(4.0, 5.0, 6.0))
        val result = a dot b
        assertEquals(
            expected = 32.0,
            actual = result,
        )
    }

    @Test
    fun `D1·D2=各行の合計との要素積`() {
        val a = IOType.d1(listOf(2.0, 3.0))
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val b = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() }
        // 1行目の合計: 1+2+3=6, 要素積: 2*6=12
        // 2行目の合計: 4+5+6=15, 要素積: 3*15=45
        val result = a dot b
        assertEquals(
            expected = IOType.d1(listOf(12.0, 45.0)),
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
        val result = a dot list
        assertEquals(
            expected = listOf(1.0, 2.0, 3.0),
            actual = result,
        )
    }

    @Test
    fun `D1·List_D2=各D2との演算結果のリスト`() {
        val a = IOType.d1(listOf(1.0, 2.0))
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )
        // 1つ目: [[1,2],[3,4]] -> 行の合計: [3,7] -> 要素積: [1*3, 2*7] = [3, 14]
        // 2つ目: [[5,6],[7,8]] -> 行の合計: [11,15] -> 要素積: [1*11, 2*15] = [11, 30]
        val result = a dot list
        assertEquals(
            expected = IOType.d1(listOf(3.0, 14.0)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(11.0, 30.0)),
            actual = result[1],
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
        val result = list1 dot list2
        assertEquals(
            expected = listOf(8.0, 32.0),
            actual = result,
        )
    }

    @Test
    fun `List_D1·List_D2=各要素同士の演算結果のリスト`() {
        val list1 =
            listOf(
                IOType.d1(listOf(1.0, 2.0)),
                IOType.d1(listOf(2.0, 3.0)),
            )
        val list2 =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )
        val result = list1 dot list2
        assertEquals(
            expected = IOType.d1(listOf(3.0, 14.0)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(22.0, 45.0)),
            actual = result[1],
        )
    }
}
