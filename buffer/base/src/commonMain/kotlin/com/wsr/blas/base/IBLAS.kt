package com.wsr.blas.base

interface IBLAS {
    val isNative: Boolean get() = false

    /**
     * Level 1 BLAS: ベクトルの内積
     * result = x^T * y
     *
     * @param x ベクトルx
     * @param y ベクトルy
     * @return 内積の結果 sum(x * y)
     */
    fun sdot(x: DataBuffer, y: DataBuffer): Float {
        var result = 0f
        repeat(x.size) {
            result += x[it] * y[it]
        }
        return result
    }

    /**
     * Level 1 BLAS: ベクトルのスカラー倍
     * x = alpha * x
     *
     * @param alpha スカラー係数
     * @param x ベクトルx
     */
    fun sscal(alpha: Float, x: DataBuffer): DataBuffer {
        val result = DataBuffer.create(x.size)
        repeat(result.size) {
            result[it] = alpha * x[it]
        }
        return result
    }

    /**
     * Level 1 BLAS: ベクトルの定数倍加算
     * y = alpha * x + y
     *
     * @param alpha スカラー係数
     * @param x ベクトルx
     * @param y ベクトルy
     */
    fun saxpy(alpha: Float, x: DataBuffer, y: DataBuffer): DataBuffer {
        val result = DataBuffer.create(x.size)
        repeat(result.size) {
            result[it] = alpha * x[it] + y[it]
        }
        return result
    }

    /**
     * Level 2 BLAS: 行列とベクトルの積
     * y = alpha * a * x + beta * y
     *
     * @param row 行列Aの行数
     * @param col 行列Aの列数
     * @param alpha スカラー係数
     * @param a 行列A (row-major, サイズ m * n)
     * @param x ベクトルx
     * @param beta スカラー係数
     * @param y ベクトルy
     */
    fun sgemv(row: Int, col: Int, alpha: Float, a: DataBuffer, x: DataBuffer, beta: Float, y: DataBuffer): DataBuffer {
        val result = DataBuffer.create(row)
        for (i in 0 until row) {
            var sum = 0f
            for (j in 0 until col) {
                sum += a[i * col + j] * x[j]
            }
            result[i] = alpha * sum + beta * y[i]
        }
        return result
    }

    /**
     * Level 3 BLAS: 行列と行列の積 (転置なし固定)
     * result[b] = alpha * a[b] * b[b] + beta * c[b]
     *
     * @param m 行列Aの行数、行列Cの行数
     * @param n 行列Bの列数、行列Cの列数
     * @param k 行列Aの列数、行列Bの行数
     * @param alpha スカラー係数
     * @param a 行列A群 (row-major, batchSize * m * k)
     * @param b 行列B群 (row-major, batchSize * k * n)
     * @param beta Cに対する係数
     * @param c 加算対象の行列C群 (row-major, batchSize * m * n)
     * @param batchSize バッチサイズ
     */
    fun sgemm(
        m: Int,
        n: Int,
        k: Int,
        alpha: Float,
        a: DataBuffer,
        b: DataBuffer,
        beta: Float,
        c: DataBuffer,
        batchSize: Int,
    ): DataBuffer {
        val result = DataBuffer.create(batchSize * m * n)
        val strideA = m * k
        val strideB = k * n
        val strideC = m * n
        for (batchIndex in 0 until batchSize) {
            val offsetA = batchIndex * strideA
            val offsetB = batchIndex * strideB
            val offsetC = batchIndex * strideC

            for (i in 0 until m) {
                for (j in 0 until n) {
                    var sum = 0f
                    for (p in 0 until k) {
                        sum += a[offsetA + i * k + p] * b[offsetB + p * n + j]
                    }
                    val index = offsetC + i * n + j
                    result[index] = alpha * sum + beta * c[index]
                }
            }
        }
        return result
    }
}
