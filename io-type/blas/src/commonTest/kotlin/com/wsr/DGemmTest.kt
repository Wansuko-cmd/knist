@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class DGemmTest {
    private val blas: IBLAS = object : IBLAS {}

    @Test
    fun `dgemm_転置なし=行列同士の積を計算する`() {
        // A = [[1, 2],
        //      [3, 4]]  (2x2)
        // B = [[5, 6],
        //      [7, 8]]  (2x2)
        // C = alpha * A * B + beta * C = 1.0 * A * B + 0.0 * C
        // C[0,0] = 1*5 + 2*7 = 19
        // C[0,1] = 1*6 + 2*8 = 22
        // C[1,0] = 3*5 + 4*7 = 43
        // C[1,1] = 3*6 + 4*8 = 50
        val a = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
        val b = doubleArrayOf(5.0, 6.0, 7.0, 8.0)
        val c = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

        blas.dgemm(
            transA = false,
            transB = false,
            m = 2,
            n = 2,
            k = 2,
            alpha = 1.0,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.0,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 19.0, actual = c[0])
        assertEquals(expected = 22.0, actual = c[1])
        assertEquals(expected = 43.0, actual = c[2])
        assertEquals(expected = 50.0, actual = c[3])
    }

    @Test
    fun `dgemm_transA=true=Aを転置して積を計算する`() {
        // A = [[1, 2],
        //      [3, 4]]  (2x2)
        // A^T = [[1, 3],
        //        [2, 4]]  (2x2)
        // B = [[5, 6],
        //      [7, 8]]  (2x2)
        // C = A^T * B
        // C[0,0] = 1*5 + 3*7 = 26
        // C[0,1] = 1*6 + 3*8 = 30
        // C[1,0] = 2*5 + 4*7 = 38
        // C[1,1] = 2*6 + 4*8 = 44
        val a = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
        val b = doubleArrayOf(5.0, 6.0, 7.0, 8.0)
        val c = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

        blas.dgemm(
            transA = true,
            transB = false,
            m = 2,
            n = 2,
            k = 2,
            alpha = 1.0,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.0,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 26.0, actual = c[0])
        assertEquals(expected = 30.0, actual = c[1])
        assertEquals(expected = 38.0, actual = c[2])
        assertEquals(expected = 44.0, actual = c[3])
    }

    @Test
    fun `dgemm_transB=true=Bを転置して積を計算する`() {
        // A = [[1, 2],
        //      [3, 4]]  (2x2)
        // B = [[5, 6],
        //      [7, 8]]  (2x2)
        // B^T = [[5, 7],
        //        [6, 8]]  (2x2)
        // C = A * B^T
        // C[0,0] = 1*5 + 2*6 = 17
        // C[0,1] = 1*7 + 2*8 = 23
        // C[1,0] = 3*5 + 4*6 = 39
        // C[1,1] = 3*7 + 4*8 = 53
        val a = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
        val b = doubleArrayOf(5.0, 6.0, 7.0, 8.0)
        val c = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

        blas.dgemm(
            transA = false,
            transB = true,
            m = 2,
            n = 2,
            k = 2,
            alpha = 1.0,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.0,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 17.0, actual = c[0])
        assertEquals(expected = 23.0, actual = c[1])
        assertEquals(expected = 39.0, actual = c[2])
        assertEquals(expected = 53.0, actual = c[3])
    }

    @Test
    fun `dgemm_alpha_beta=スカラー係数を適用する`() {
        // A = [[1, 2],
        //      [3, 4]]
        // B = [[1, 0],
        //      [0, 1]]  (単位行列)
        // C = [[10, 20],
        //      [30, 40]]
        // result = 2.0 * A * B + 0.5 * C = 2.0 * A + 0.5 * C
        // C[0,0] = 2.0 * 1 + 0.5 * 10 = 7
        // C[0,1] = 2.0 * 2 + 0.5 * 20 = 14
        // C[1,0] = 2.0 * 3 + 0.5 * 30 = 21
        // C[1,1] = 2.0 * 4 + 0.5 * 40 = 28
        val a = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
        val b = doubleArrayOf(1.0, 0.0, 0.0, 1.0)
        val c = doubleArrayOf(10.0, 20.0, 30.0, 40.0)

        blas.dgemm(
            transA = false,
            transB = false,
            m = 2,
            n = 2,
            k = 2,
            alpha = 2.0,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.5,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 7.0, actual = c[0])
        assertEquals(expected = 14.0, actual = c[1])
        assertEquals(expected = 21.0, actual = c[2])
        assertEquals(expected = 28.0, actual = c[3])
    }

    @Test
    fun `dgemm_非正方行列=異なるサイズの行列同士の積を計算する`() {
        // A = [[1, 2, 3],
        //      [4, 5, 6]]  (2x3)
        // B = [[1, 2],
        //      [3, 4],
        //      [5, 6]]  (3x2)
        // C = A * B  (2x2)
        // C[0,0] = 1*1 + 2*3 + 3*5 = 22
        // C[0,1] = 1*2 + 2*4 + 3*6 = 28
        // C[1,0] = 4*1 + 5*3 + 6*5 = 49
        // C[1,1] = 4*2 + 5*4 + 6*6 = 64
        val a = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
        val b = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
        val c = doubleArrayOf(0.0, 0.0, 0.0, 0.0)

        blas.dgemm(
            transA = false,
            transB = false,
            m = 2,
            n = 2,
            k = 3,
            alpha = 1.0,
            a = a,
            lda = 3,
            b = b,
            ldb = 2,
            beta = 0.0,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 22.0, actual = c[0])
        assertEquals(expected = 28.0, actual = c[1])
        assertEquals(expected = 49.0, actual = c[2])
        assertEquals(expected = 64.0, actual = c[3])
    }
}
