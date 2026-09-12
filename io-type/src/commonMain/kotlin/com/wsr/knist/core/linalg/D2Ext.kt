package com.wsr.knist.core.linalg

import com.wsr.knist.Backend
import com.wsr.knist.core.D1
import com.wsr.knist.core.D2
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun IOType.D2.matMul(other: IOType.D1, @ScopeOpDefault("false") trans: Boolean = false): IOType.D1.Global {
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

@ScopeOp
fun IOType.D2.matMul(other: IOType.D2, @ScopeOpDefault("false") transA: Boolean = false, @ScopeOpDefault("false") transB: Boolean = false): IOType.D2.Global {
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
