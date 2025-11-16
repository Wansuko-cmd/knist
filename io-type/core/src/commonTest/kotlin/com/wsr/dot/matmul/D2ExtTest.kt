@file:Suppress("NonAsciiCharacters")

package com.wsr.dot.matmul

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class D2ExtTest {
    @Test
    fun `D2·D1=行列とベクトルの積`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val b = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        // 1*1 + 2*2 + 3*3 = 14
        // 4*1 + 5*2 + 6*3 = 32
        val result = a matMul b
        assertEquals(
            expected = IOType.d1(listOf(14.0f, 32.0f)),
            actual = result,
        )
    }

    @Test
    fun `D2·D2=行列と行列の積`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val b = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        // [[1*1+2*3+3*5, 1*2+2*4+3*6], [4*1+5*3+6*5, 4*2+5*4+6*6]]
        // [[22, 28], [49, 64]]
        val result = a matMul b
        assertEquals(
            expected =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 22.0f
                    x == 0 && y == 1 -> 28.0f
                    x == 1 && y == 0 -> 49.0f
                    else -> 64.0f
                }
            },
            actual = result,
        )
    }

    @Test
    fun `D2·List_D1=各D1との積のリスト`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val list =
            listOf(
                IOType.d1(listOf(1.0f, 0.0f, 0.0f)),
                IOType.d1(listOf(0.0f, 1.0f, 0.0f)),
            )
        val result = a matMul list
        assertEquals(
            expected = IOType.d1(listOf(1.0f, 4.0f)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(2.0f, 5.0f)),
            actual = result[1],
        )
    }

    @Test
    fun `D2·List_D2=各D2との積のリスト`() {
        val a = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        // [[1,2],[3,4]] dot [[1,2],[3,4]] = [[7,10],[15,22]]
        val result = a matMul list
        assertEquals(
            expected =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 7.0f
                    x == 0 && y == 1 -> 10.0f
                    x == 1 && y == 0 -> 15.0f
                    else -> 22.0f
                }
            },
            actual = result[0],
        )
        // [[1,2],[3,4]] dot [[5,6],[7,8]] = [[19,22],[43,50]]
        assertEquals(
            expected =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 19.0f
                    x == 0 && y == 1 -> 22.0f
                    x == 1 && y == 0 -> 43.0f
                    else -> 50.0f
                }
            },
            actual = result[1],
        )
    }

    @Test
    fun `List_D2·List_D1=各要素同士の積のリスト`() {
        val list1 =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val list2 =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f)),
                IOType.d1(listOf(3.0f, 4.0f)),
            )
        // [[1,2],[3,4]] · [1,2] = [5, 11]
        // [[5,6],[7,8]] · [3,4] = [39, 53]
        val result = list1 matMul list2
        assertEquals(
            expected = IOType.d1(listOf(5.0f, 11.0f)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(39.0f, 53.0f)),
            actual = result[1],
        )
    }

    @Test
    fun `List_D2·List_D2=各要素同士の積のリスト`() {
        val list1 =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val list2 =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list1 matMul list2
        assertEquals(
            expected =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 7.0f
                    x == 0 && y == 1 -> 10.0f
                    x == 1 && y == 0 -> 15.0f
                    else -> 22.0f
                }
            },
            actual = result[0],
        )
        assertEquals(
            expected =
            IOType.d2(2, 2) { x, y ->
                when {
                    x == 0 && y == 0 -> 67.0f
                    x == 0 && y == 1 -> 78.0f
                    x == 1 && y == 0 -> 91.0f
                    else -> 106.0f
                }
            },
            actual = result[1],
        )
    }
}
