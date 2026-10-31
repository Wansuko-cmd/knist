package com.wsr.knist.batch.linalg

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d0
import com.wsr.knist.batch.d1
import com.wsr.knist.batch.i
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchD1sInnerD1s")
@ScopeOp
infix fun Batch<IOType.D1>.inner(other: Batch<IOType.D1>): Batch<IOType.D0.Global> {
    val result = Backend.inner(x = value, y = other.value, b = size)
    return Batch.d0(size, result)
}

@JvmName("batchD1sMatmulD2")
@ScopeOp
fun Batch<IOType.D1>.matMul(other: IOType.D2, @ScopeOpDefault("false") trans: Boolean = false): Batch<IOType.D1.Global> {
    val n = if (trans) other.i else other.j
    val result = Backend.matMul(
        x = value,
        transX = false,
        y = other.value,
        transY = trans,
        m = size,
        n = n,
        k = this.i,
        b = 1,
    )
    return Batch.d1(size = size, i = n, value = result)
}
