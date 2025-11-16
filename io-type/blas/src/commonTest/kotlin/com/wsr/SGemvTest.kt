@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class SGemvTest {
    private val blas: IBLAS = object : IBLAS {}

    @Test
    fun `sgemv_trans=false=行列とベクトルの積を計算する`() {
        // A = [[1, 2, 3],
        //      [4, 5, 6]]  (2x3 行列)
        // x = [1, 2, 3]
        // y = alpha * A * x + beta * y = 1.0 * A * x + 0.0 * y
        // y[0] = 1*1 + 2*2 + 3*3 = 14
        // y[1] = 4*1 + 5*2 + 6*3 = 32
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)
        val x = floatArrayOf(1.0f, 2.0f, 3.0f)
        val y = floatArrayOf(0.0f, 0.0f)

        blas.sgemv(
            trans = false,
            m = 2,
            n = 3,
            alpha = 1.0f,
            a = a,
            lda = 3,
            x = x,
            incX = 1,
            beta = 0.0f,
            y = y,
            incY = 1,
        )

        assertEquals(expected = 14.0f, actual = y[0])
        assertEquals(expected = 32.0f, actual = y[1])
    }

    @Test
    fun `sgemv_trans=true=転置行列とベクトルの積を計算する`() {
        // A = [[1, 2, 3],
        //      [4, 5, 6]]  (2x3 行列)
        // A^T = [[1, 4],
        //        [2, 5],
        //        [3, 6]]  (3x2 行列)
        // x = [1, 2]
        // y = alpha * A^T * x + beta * y = 1.0 * A^T * x + 0.0 * y
        // y[0] = 1*1 + 4*2 = 9
        // y[1] = 2*1 + 5*2 = 12
        // y[2] = 3*1 + 6*2 = 15
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)
        val x = floatArrayOf(1.0f, 2.0f)
        val y = floatArrayOf(0.0f, 0.0f, 0.0f)

        blas.sgemv(
            trans = true,
            m = 2,
            n = 3,
            alpha = 1.0f,
            a = a,
            lda = 3,
            x = x,
            incX = 1,
            beta = 0.0f,
            y = y,
            incY = 1,
        )

        assertEquals(expected = 9.0f, actual = y[0])
        assertEquals(expected = 12.0f, actual = y[1])
        assertEquals(expected = 15.0f, actual = y[2])
    }

    @Test
    fun `sgemv_alpha_beta=スカラー係数を適用する`() {
        // A = [[1, 2],
        //      [3, 4]]
        // x = [1, 1]
        // y = [10, 20]
        // result = 2.0 * A * x + 0.5 * y
        // y[0] = 2.0 * (1+2) + 0.5 * 10 = 6 + 5 = 11
        // y[1] = 2.0 * (3+4) + 0.5 * 20 = 14 + 10 = 24
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f)
        val x = floatArrayOf(1.0f, 1.0f)
        val y = floatArrayOf(10.0f, 20.0f)

        blas.sgemv(
            trans = false,
            m = 2,
            n = 2,
            alpha = 2.0f,
            a = a,
            lda = 2,
            x = x,
            incX = 1,
            beta = 0.5f,
            y = y,
            incY = 1,
        )

        assertEquals(expected = 11.0f, actual = y[0])
        assertEquals(expected = 24.0f, actual = y[1])
    }
}
