package com.wsr.batch.operation.matmul

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

fun IOType.D2.matMul(other: Batch<IOType.D1>, trans: Boolean = false): Batch<IOType.D1> {
    val m = if (trans) shape[1] else shape[0]
    val k = if (trans) shape[0] else shape[1]
    val result = Backend.matMul(
        x = value,
        transX = trans,
        y = other.value,
        transY = false,
        m = m,
        n = 1,
        k = k,
        b = other.size,
    )

    return Batch(result, other.size, listOf(m))
}

@JvmName("matMulToD2s")
fun Batch<IOType.D2>.matMul(other: IOType.D2, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D2> {
    val m = if (transA) shape[1] else shape[0]
    val n = if (transB) other.shape[0] else other.shape[1]
    val k = if (transA) shape[0] else shape[1]
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = size * m,
        n = n,
        k = k,
        b = 1,
    )
    return Batch(value = result, size = size, shape = listOf(m, n))
}

@JvmName("matMulToD2s")
fun Batch<IOType.D2>.matMul(
    other: Batch<IOType.D2>,
    transA: Boolean = false,
    transB: Boolean = false,
): Batch<IOType.D2> {
    val m = if (transA) shape[1] else shape[0]
    val n = if (transB) other.shape[0] else other.shape[1]
    val k = if (transA) shape[0] else shape[1]
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = m,
        n = n,
        k = k,
        b = size,
    )
    return Batch(value = result, size = size, shape = listOf(m, n))
}
