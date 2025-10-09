package com.wsr

expect object BLAS : IBLAS

interface IBLAS {
    val isNative: Boolean get() = false

    /**
     * Level 1 BLAS: ベクトルの内積
     * result = x^T * y
     *
     * ベクトル同士の内積計算に使用
     *
     * @param n ベクトルの要素数
     * @param x ベクトルx
     * @param incX ベクトルxのストライド (通常は1、配列の何要素ごとにアクセスするか)
     * @param y ベクトルy
     * @param incY ベクトルyのストライド (通常は1、配列の何要素ごとにアクセスするか)
     * @return 内積の結果 sum(x[i] * y[i])
     */
    fun ddot(n: Int, x: DoubleArray, incX: Int, y: DoubleArray, incY: Int): Double {
        var result = 0.0
        var xi = 0
        var yi = 0

        repeat(n) {
            result += x[xi] * y[yi]
            xi += incX
            yi += incY
        }

        return result
    }

    /**
     * Level 2 BLAS: 行列とベクトルの積
     * y = alpha * op(A) * x + beta * y
     * op(A) = A (trans=false) または A^T (trans=true)
     *
     * 全結合層の順伝播・逆伝播計算に使用
     *
     * @param trans trueの場合、行列Aを転置して使用
     * @param m 行列Aの行数 (trans=falseの場合の出力ベクトルyのサイズ)
     * @param n 行列Aの列数 (trans=falseの場合の入力ベクトルxのサイズ)
     * @param alpha スカラー係数
     * @param a 行列A (row-major, サイズ m * n)
     * @param lda 行列Aの先頭次元 (row-majorの場合は列数n)
     * @param x ベクトルx (サイズ: trans=false なら n、trans=true なら m)
     * @param incX ベクトルxのストライド
     * @param beta スカラー係数
     * @param y ベクトルy (サイズ: trans=false なら m、trans=true なら n、入出力)
     * @param incY ベクトルyのストライド
     */
    fun dgemv(
        trans: Boolean,
        m: Int,
        n: Int,
        alpha: Double,
        a: DoubleArray,
        lda: Int,
        x: DoubleArray,
        incX: Int,
        beta: Double,
        y: DoubleArray,
        incY: Int,
    ) {
        val rows = if (trans) n else m
        val cols = if (trans) m else n

        var yi = 0
        for (i in 0 until rows) {
            var sum = 0.0
            var xi = 0
            for (j in 0 until cols) {
                val aVal = if (trans) a[j * lda + i] else a[i * lda + j]
                sum += aVal * x[xi]
                xi += incX
            }
            y[yi] = alpha * sum + beta * y[yi]
            yi += incY
        }
    }

    /**
     * Level 3 BLAS: 行列と行列の積
     * C = alpha * op(A) * op(B) + beta * C
     * op(X) = X (transX=false) または X^T (transX=true)
     *
     * 全結合層や畳み込み層(im2col使用時)の計算に使用
     *
     * @param transA trueの場合、行列Aを転置して使用
     * @param transB trueの場合、行列Bを転置して使用
     * @param m op(A)の行数、行列Cの行数
     * @param n op(B)の列数、行列Cの列数
     * @param k op(A)の列数、op(B)の行数
     * @param alpha スカラー係数
     * @param a 行列A (row-major, サイズ: transA=false なら m*k、transA=true なら k*m)
     * @param lda 行列Aの先頭次元 (row-majorの場合、transA=false なら k、transA=true なら m)
     * @param b 行列B (row-major, サイズ: transB=false なら k*n、transB=true なら n*k)
     * @param ldb 行列Bの先頭次元 (row-majorの場合、transB=false なら n、transB=true なら k)
     * @param beta スカラー係数
     * @param c 行列C (row-major, サイズ m*n、入出力)
     * @param ldc 行列Cの先頭次元 (row-majorの場合は列数n)
     */
    fun dgemm(
        transA: Boolean,
        transB: Boolean,
        m: Int,
        n: Int,
        k: Int,
        alpha: Double,
        a: DoubleArray,
        lda: Int,
        b: DoubleArray,
        ldb: Int,
        beta: Double,
        c: DoubleArray,
        ldc: Int,
    ) {
        // Default implementation: naive matrix multiplication
        for (i in 0 until m) {
            for (j in 0 until n) {
                var sum = 0.0
                for (p in 0 until k) {
                    val aVal = if (transA) a[p * lda + i] else a[i * lda + p]
                    val bVal = if (transB) b[j * ldb + p] else b[p * ldb + j]
                    sum += aVal * bVal
                }
                c[i * ldc + j] = alpha * sum + beta * c[i * ldc + j]
            }
        }
    }
}
