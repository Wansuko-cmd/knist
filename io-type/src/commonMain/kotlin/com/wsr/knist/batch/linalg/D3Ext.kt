package com.wsr.knist.batch.linalg

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@ScopeOp
fun IOType.D3.matMul(other: Batch<IOType.D3>, transA: Boolean = false, transB: Boolean = false): Batch<IOType.D3.Global> {
    val m = if (transA) k else j
    val n = if (transB) other.j else other.k
    val k = if (transA) j else k
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = m,
        n = size * n,
        k = k,
        b = i,
    )
    return Batch.d3(other.size, i, m, n, result)
}

@JvmName("batchD3sMatMulD3")
@ScopeOp
fun Batch<IOType.D3>.matMul(other: IOType.D3, @ScopeOpDefault("false") transA: Boolean = false, @ScopeOpDefault("false") transB: Boolean = false): Batch<IOType.D3.Global> {
    val m = if (transA) k else j
    val n = if (transB) other.j else other.k
    val k = if (transA) j else k
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = size * m,
        n = n,
        k = k,
        b = i,
    )
    return Batch.d3(size, i, m, n, result)
}

@JvmName("batchD3sMatMulD3s")
@ScopeOp
fun Batch<IOType.D3>.matMul(other: Batch<IOType.D3>, @ScopeOpDefault("false") transA: Boolean = false, @ScopeOpDefault("false") transB: Boolean = false): Batch<IOType.D3.Global> {
    val m = if (transA) this.k else this.j
    val n = if (transB) other.j else other.k
    val k = if (transA) this.j else this.k
    val result = Backend.matMul(
        x = value,
        transX = transA,
        y = other.value,
        transY = transB,
        m = m,
        n = n,
        k = k,
        b = size * i,
    )
    return Batch.d3(size, i, m, n, result)
}
