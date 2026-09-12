package com.wsr.knist.core.linalg

import com.wsr.knist.Backend
import com.wsr.knist.core.D3
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun IOType.D3.matMul(other: IOType.D3, @ScopeOpDefault("false") transA: Boolean = false, @ScopeOpDefault("false") transB: Boolean = false): IOType.D3.Global {
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
        b = shape[0],
    )
    return IOType.D3(shape = listOf(shape[0], m, n), value = result)
}
