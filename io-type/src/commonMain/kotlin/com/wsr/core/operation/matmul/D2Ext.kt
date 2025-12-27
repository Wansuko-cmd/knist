package com.wsr.core.operation.matmul

import com.wsr.Backend
import com.wsr.core.IOType

fun IOType.D2.matMul(other: IOType.D1, trans: Boolean = false): IOType.D1 {
    val m = if (trans) shape[1] else shape[0]
    val k = if (trans) shape[0] else shape[1]
    val result = Backend.matMul(
        x = value,
        transX = trans,
        y = other.value,
        m = m,
        k = k,
    )
    return IOType.D1(result)
}

fun IOType.D2.matMul(other: IOType.D2, transA: Boolean = false, transB: Boolean = false): IOType.D2 {
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
        b = 1,
    )
    return IOType.D2(result, listOf(m, n))
}
