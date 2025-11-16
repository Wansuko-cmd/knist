package com.wsr.collection

import com.wsr.BLAS
import com.wsr.IOType
import com.wsr.reshape.transpose

fun IOType.D1.average(): Float = value.average().toFloat()

@JvmName("averageToD1s")
fun List<IOType.D1>.average(): List<Float> = map { it.average() }

fun IOType.D2.average(): Float = value.average().toFloat()

@JvmName("averageToD2s")
fun List<IOType.D2>.average(): List<Float> = map { it.average() }

fun IOType.D2.average(axis: Int) = when (axis) {
    0 -> {
        // 列方向の平均: 各列の要素を合計して要素数で割る
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
        // 行数で割って平均を計算
        BLAS.sscal(n = result.size, alpha = 1f / shape[0], x = result, incX = 1)
        IOType.d1(result)
    }

    1 -> {
        // 行方向の平均: 各行の要素を合計して要素数で割る
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
        // 列数で割って平均を計算
        BLAS.sscal(n = result.size, alpha = 1f / shape[1], x = result, incX = 1)
        IOType.d1(result)
    }

    else -> throw IllegalArgumentException("IOType.D2.max axis is $axis not 0 or 1.")
}

@JvmName("averageAxisToD2s")
fun List<IOType.D2>.average(axis: Int) = map { it.average(axis) }

fun IOType.D3.average(): Float = value.average().toFloat()

@JvmName("averageToD3s")
fun List<IOType.D3>.average(): List<Float> = map { it.average() }

fun IOType.D3.average(axis: Int) = when (axis) {
    0 -> {
        val transpose = transpose(1, 2, 0)
        IOType.d2(shape[1], shape[2]) { y, z -> transpose[y, z].average() }
    }

    1 -> {
        val transpose = transpose(0, 2, 1)
        IOType.d2(shape[0], shape[2]) { x, z -> transpose[x, z].average() }
    }

    2 -> IOType.d2(shape[0], shape[1]) { x, y -> this[x, y].average() }
    else -> throw IllegalArgumentException("IOType.D3.max axis is $axis not 0, 1 or 2.")
}

@JvmName("averageAxisToD3s")
fun List<IOType.D3>.average(axis: Int) = map { it.average(axis) }
