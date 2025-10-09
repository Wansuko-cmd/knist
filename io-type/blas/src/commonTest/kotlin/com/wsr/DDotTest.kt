@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class DDotTest {
    private val blas: IBLAS = object : IBLAS {}

    @Test
    fun `ddot=2つのベクトルの内積を計算する`() {
        // x = [1.0, 2.0, 3.0]
        // y = [4.0, 5.0, 6.0]
        // result = 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
        val x = doubleArrayOf(1.0, 2.0, 3.0)
        val y = doubleArrayOf(4.0, 5.0, 6.0)

        val result = blas.ddot(
            n = 3,
            x = x,
            incX = 1,
            y = y,
            incY = 1,
        )

        assertEquals(expected = 32.0, actual = result)
    }

    @Test
    fun `ddot_stride=2=ストライドを指定して内積を計算する`() {
        // x = [1.0, 99.0, 2.0, 99.0, 3.0]  (incX=2でアクセス: [1.0, 2.0, 3.0])
        // y = [4.0, 99.0, 5.0, 99.0, 6.0]  (incY=2でアクセス: [4.0, 5.0, 6.0])
        // result = 1*4 + 2*5 + 3*6 = 32
        val x = doubleArrayOf(1.0, 99.0, 2.0, 99.0, 3.0)
        val y = doubleArrayOf(4.0, 99.0, 5.0, 99.0, 6.0)

        val result = blas.ddot(
            n = 3,
            x = x,
            incX = 2,
            y = y,
            incY = 2,
        )

        assertEquals(expected = 32.0, actual = result)
    }

    @Test
    fun `ddot_ゼロベクトル=0を返す`() {
        val x = doubleArrayOf(1.0, 2.0, 3.0)
        val y = doubleArrayOf(0.0, 0.0, 0.0)

        val result = blas.ddot(
            n = 3,
            x = x,
            incX = 1,
            y = y,
            incY = 1,
        )

        assertEquals(expected = 0.0, actual = result)
    }
}
