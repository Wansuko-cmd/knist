package com.wsr.knist.batch.linalg

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.d2
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName
@ScopeOp
fun IOType.D2.matMul(other: Batch<IOType.D1>, @ScopeOpDefault("false") trans: Boolean = false): Batch<IOType.D1.Global> {
    val n = if (trans) j else i
    val k = if (trans) i else j
    val result = Backend.matMul(
        x = other.value,
        transX = false,
        y = value,
        transY = !trans,
        m = other.size,
        n = n,
        k = k,
        b = 1,
    )

    return Batch.d1(other.size, n, result)
}

@JvmName("batchD2sMatMulD2")
@ScopeOp
fun Batch<IOType.D2>.matMul(other: IOType.D2, @ScopeOpDefault("false") transA: Boolean = false, @ScopeOpDefault("false") transB: Boolean = false): Batch<IOType.D2.Global> {
    val m = if (transA) j else i
    val n = if (transB) other.i else other.j
    val k = if (transA) i else j
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
    return Batch.d2(size, m, n, result)
}

@JvmName("batchD2sMatMulD2s")
@ScopeOp
fun Batch<IOType.D2>.matMul(other: Batch<IOType.D2>, @ScopeOpDefault("false") transA: Boolean = false, @ScopeOpDefault("false") transB: Boolean = false): Batch<IOType.D2.Global> {
    val m = if (transA) j else i
    val n = if (transB) other.i else other.j
    val k = if (transA) i else j
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
    return Batch.d2(size, m, n, result)
}
