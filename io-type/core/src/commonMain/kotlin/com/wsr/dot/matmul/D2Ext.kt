package com.wsr.dot.matmul

import com.wsr.BLAS
import com.wsr.IOType

infix fun IOType.D2.matMul(other: IOType.D1): IOType.D1 {
    val result = FloatArray(shape[0])
    BLAS.sgemv(
        trans = false,
        m = shape[0],
        n = shape[1],
        alpha = 1f,
        a = value,
        lda = shape[1],
        x = other.value,
        incX = 1,
        beta = 0f,
        y = result,
        incY = 1,
    )
    return IOType.D1(result)
}

infix fun IOType.D2.matMul(other: IOType.D2): IOType.D2 {
    val result = FloatArray(shape[0] * other.shape[1])
    BLAS.sgemm(
        transA = false,
        transB = false,
        m = shape[0],
        n = other.shape[1],
        k = shape[1],
        alpha = 1f,
        a = value,
        lda = shape[1],
        b = other.value,
        ldb = other.shape[1],
        beta = 0f,
        c = result,
        ldc = other.shape[1],
    )
    return IOType.D2(result, listOf(shape[0], other.shape[1]))
}

@JvmName("matMulToD2s")
infix fun List<IOType.D2>.matMul(other: IOType.D2) = List(size) { this[it] matMul other }

@JvmName("matMulToD2s")
infix fun List<IOType.D2>.matMul(other: List<IOType.D2>) = List(size) { this[it] matMul other[it] }
