package com.wsr.conv

import com.wsr.BLAS

infix fun Array<DoubleArray>.dot(other: Array<DoubleArray>): Array<DoubleArray> {
    val m = other.size
    val n = size
    val k = this[0].size

    // 入力行列を1次元配列に変換 (row-major)
    val aFlat = DoubleArray(n * k)
    for (i in 0 until n) {
        for (j in 0 until k) {
            aFlat[i * k + j] = this[i][j]
        }
    }

    val bFlat = DoubleArray(m * k)
    for (i in 0 until m) {
        for (j in 0 until k) {
            bFlat[i * k + j] = other[i][j]
        }
    }

    val cFlat = DoubleArray(m * n)

    // C[m, n] = A[n, k]^T * B[m, k]^T = B[m, k] * A[n, k]^T
    // other[f] dot this[i] = sum_k(other[f][k] * this[i][k])
    // result[f][i] = other[f] dot this[i]
    BLAS.dgemm(
        transA = false,
        transB = true,
        m = m,
        n = n,
        k = k,
        alpha = 1.0,
        a = bFlat,
        lda = k,
        b = aFlat,
        ldb = k,
        beta = 0.0,
        c = cFlat,
        ldc = n,
    )

    // 結果を2次元配列に変換
    val result = Array(m) { DoubleArray(n) }
    for (i in 0 until m) {
        for (j in 0 until n) {
            result[i][j] = cFlat[i * n + j]
        }
    }

    return result
}

infix fun DoubleArray.dot(other: DoubleArray): Double = BLAS.ddot(
    n = this.size,
    x = this,
    incX = 1,
    y = other,
    incY = 1,
)
