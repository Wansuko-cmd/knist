package com.wsr.batch.operation.matmul

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

fun IOType.D3.matMul(other: Batch<IOType.D3>, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D3> {
    val m = if (transA) shape[2] else shape[1]
    val n = if (transB) other.shape[1] else other.shape[2]
    val k = if (transA) shape[1] else shape[2]
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = m,
        n = size * n,
        k = k,
        b = shape[0],
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], m, n))
}

@JvmName("matMulToD3s")
fun Batch<IOType.D3>.matMul(other: IOType.D3, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D3> {
    val m = if (transA) shape[2] else shape[1]
    val n = if (transB) other.shape[1] else other.shape[2]
    val k = if (transA) shape[1] else shape[2]
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = size * m,
        n = n,
        k = k,
        b = shape[0],
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], m, n))
}

@JvmName("matMulToD3s")
fun Batch<IOType.D3>.matMul(
    other: Batch<IOType.D3>,
    transA: Boolean = false,
    transB: Boolean = false,
): Batch<IOType.D3> {
    val m = if (transA) shape[2] else shape[1]
    val n = if (transB) other.shape[1] else other.shape[2]
    val k = if (transA) shape[1] else shape[2]
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = m,
        n = n,
        k = k,
        b = size * shape[0],
    )
    return Batch(value = result, size = size, shape = listOf(shape[0], m, n))
}
