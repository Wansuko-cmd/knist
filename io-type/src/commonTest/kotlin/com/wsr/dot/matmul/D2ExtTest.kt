@file:Suppress("NonAsciiCharacters")

package com.wsr.dot.matmul

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.operation.matmul.matMul
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
        val result = a.matMul(b)
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
        val result = a.matMul(b)
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
}
