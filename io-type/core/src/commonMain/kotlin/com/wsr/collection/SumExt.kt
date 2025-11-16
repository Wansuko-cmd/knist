package com.wsr.collection

import com.wsr.BLAS
import com.wsr.IOType

fun IOType.D1.sum() = value.sum()

fun List<IOType.D1>.sum(): List<Float> = map { it.sum() }

fun IOType.D2.sum() = value.sum()

fun IOType.D2.sum(axis: Int): IOType.D1 = when (axis) {
    0 -> {
        // 列方向の合計: 各列の要素を合計
        val ones = FloatArray(shape[0]) { 1f }
        val result = FloatArray(shape[1])
        BLAS.sgemv(
            trans = true,
            m = shape[0],
            n = shape[1],
            alpha = 1f,
            a = value,
            lda = shape[1],
            x = ones,
            incX = 1,
            beta = 0f,
            y = result,
            incY = 1,
        )
        IOType.d1(result)
    }

    1 -> {
        // 行方向の合計: 各行の要素を合計
        val ones = FloatArray(shape[1]) { 1f }
        val result = FloatArray(shape[0])
        BLAS.sgemv(
            trans = false,
            m = shape[0],
            n = shape[1],
            alpha = 1f,
            a = value,
            lda = shape[1],
            x = ones,
            incX = 1,
            beta = 0f,
            y = result,
            incY = 1,
        )
        IOType.d1(result)
    }

    else -> throw IllegalArgumentException("IOType.D2.sum axis is $axis not 0 or 1.")
}

@JvmName("sumD2sWithAxis")
fun List<IOType.D2>.sum(axis: Int): List<IOType.D1> = map { it.sum(axis) }

fun IOType.D3.sum() = value.sum()
