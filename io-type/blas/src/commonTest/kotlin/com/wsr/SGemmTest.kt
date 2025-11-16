@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class SGemmTest {
    private val blas: IBLAS = object : IBLAS {}

    @Test
    fun `sgemm_転置なし=行列同士の積を計算する`() {
        // A = [[1, 2],
        //      [3, 4]]  (2x2)
        // B = [[5, 6],
        //      [7, 8]]  (2x2)
        // C = alpha * A * B + beta * C = 1.0 * A * B + 0.0 * C
        // C[0,0] = 1*5 + 2*7 = 19
        // C[0,1] = 1*6 + 2*8 = 22
        // C[1,0] = 3*5 + 4*7 = 43
        // C[1,1] = 3*6 + 4*8 = 50
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f)
        val b = floatArrayOf(5.0f, 6.0f, 7.0f, 8.0f)
        val c = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        blas.sgemm(
            transA = false,
            transB = false,
            m = 2,
            n = 2,
            k = 2,
            alpha = 1.0f,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.0f,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 19.0f, actual = c[0])
        assertEquals(expected = 22.0f, actual = c[1])
        assertEquals(expected = 43.0f, actual = c[2])
        assertEquals(expected = 50.0f, actual = c[3])
    }

    @Test
    fun `sgemm_transA=true=Aを転置して積を計算する`() {
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
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f)
        val b = floatArrayOf(5.0f, 6.0f, 7.0f, 8.0f)
        val c = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        blas.sgemm(
            transA = true,
            transB = false,
            m = 2,
            n = 2,
            k = 2,
            alpha = 1.0f,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.0f,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 26.0f, actual = c[0])
        assertEquals(expected = 30.0f, actual = c[1])
        assertEquals(expected = 38.0f, actual = c[2])
        assertEquals(expected = 44.0f, actual = c[3])
    }

    @Test
    fun `sgemm_transB=true=Bを転置して積を計算する`() {
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
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f)
        val b = floatArrayOf(5.0f, 6.0f, 7.0f, 8.0f)
        val c = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        blas.sgemm(
            transA = false,
            transB = true,
            m = 2,
            n = 2,
            k = 2,
            alpha = 1.0f,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.0f,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 17.0f, actual = c[0])
        assertEquals(expected = 23.0f, actual = c[1])
        assertEquals(expected = 39.0f, actual = c[2])
        assertEquals(expected = 53.0f, actual = c[3])
    }

    @Test
    fun `sgemm_alpha_beta=スカラー係数を適用する`() {
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
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f)
        val b = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        val c = floatArrayOf(10.0f, 20.0f, 30.0f, 40.0f)

        blas.sgemm(
            transA = false,
            transB = false,
            m = 2,
            n = 2,
            k = 2,
            alpha = 2.0f,
            a = a,
            lda = 2,
            b = b,
            ldb = 2,
            beta = 0.5f,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 7.0f, actual = c[0])
        assertEquals(expected = 14.0f, actual = c[1])
        assertEquals(expected = 21.0f, actual = c[2])
        assertEquals(expected = 28.0f, actual = c[3])
    }

    @Test
    fun `sgemm_非正方行列=異なるサイズの行列同士の積を計算する`() {
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
        val a = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)
        val b = floatArrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)
        val c = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        blas.sgemm(
            transA = false,
            transB = false,
            m = 2,
            n = 2,
            k = 3,
            alpha = 1.0f,
            a = a,
            lda = 3,
            b = b,
            ldb = 2,
            beta = 0.0f,
            c = c,
            ldc = 2,
        )

        assertEquals(expected = 22.0f, actual = c[0])
        assertEquals(expected = 28.0f, actual = c[1])
        assertEquals(expected = 49.0f, actual = c[2])
        assertEquals(expected = 64.0f, actual = c[3])
    }
}
