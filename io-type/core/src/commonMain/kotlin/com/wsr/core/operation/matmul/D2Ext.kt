package com.wsr.core.operation.matmul

import com.wsr.BLAS
import com.wsr.blas.base.DataBuffer
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.set

infix fun IOType.D2.matMul(other: IOType.D1): IOType.D1 {
    val result = BLAS.sgemv2(
        row = shape[0],
        col = shape[1],
        alpha = 1f,
        a = value,
        x = other.value,
        beta = 0f,
        y = DataBuffer.create(shape[0]),
    )
    return IOType.D1(result)
}

infix fun IOType.D2.matMul(other: IOType.D2): IOType.D2 {
    val result = BLAS.sgemm2(
        m = shape[0],
        n = other.shape[1],
        k = shape[1],
        alpha = 1f,
        a = value,
        b = other.value,
        beta = 0f,
        c = DataBuffer.create(shape[0] * other.shape[1]),
    )
    return IOType.D2(result, listOf(shape[0], other.shape[1]))
}

@JvmName("matMulToD2s")
infix fun List<IOType.D2>.matMul(other: IOType.D2) = List(size) { this[it] matMul other }

@JvmName("matMulToD2s")
infix fun List<IOType.D2>.matMul(other: List<IOType.D2>) = List(size) { this[it] matMul other[it] }
