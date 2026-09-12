package com.wsr.knist.batch.elementwise.operation.minus

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@JvmName("batchD3sMinusFloat")
@ScopeOp
operator fun Batch<IOType.D3>.minus(other: Float): Batch<IOType.D3.Global> {
    val result = Backend.minus(x = value, y = other)
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sMinusD0s")
@ScopeOp
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D0>): Batch<IOType.D3.Global> {
    val result = Backend.minus(x = value, xi = size, xj = step, y = other.value, axis = 0)
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sMinusD1WithAxis")
@ScopeOp
fun Batch<IOType.D3>.minus(other: IOType.D1, axis: Int): Batch<IOType.D3.Global> {
    val result = Backend.minus(
        x = value,
        xi = size,
        xj = i,
        xk = j,
        xl = k,
        y = other.value,
        axis = axis + 1,
    )
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sMinusD2")
@ScopeOp
operator fun Batch<IOType.D3>.minus(other: IOType.D2): Batch<IOType.D3.Global> = minus(other = other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sMinusD2WithAxis")
@ScopeOp
fun Batch<IOType.D3>.minus(other: IOType.D2, axis1: Int, axis2: Int): Batch<IOType.D3.Global> {
    val result = Backend.minus(
        x = value,
        xi = size,
        xj = i,
        xk = j,
        xl = k,
        y = other.value,
        yi = other.i,
        yj = other.j,
        axis1 = axis1 + 1,
        axis2 = axis2 + 1,
    )
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sMinusD2s")
@ScopeOp
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D2>) = minus(other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sMinusD2sWithAxis")
@ScopeOp
fun Batch<IOType.D3>.minus(other: Batch<IOType.D2>, axis1: Int, axis2: Int): Batch<IOType.D3.Global> {
    val result = Backend.minus(
        x = value,
        xi = size,
        xj = i,
        xk = j,
        xl = k,
        y = other.value,
        yi = other.size,
        yj = other.i,
        yk = other.j,
        axis1 = 0,
        axis2 = axis1 + 1,
        axis3 = axis2 + 1,
    )
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sMinusD3")
@ScopeOp
operator fun Batch<IOType.D3>.minus(other: IOType.D3): Batch<IOType.D3.Global> {
    val result = Backend.minus(
        x = value,
        xi = size,
        xj = step,
        y = other.value,
        axis = 1,
    )
    return Batch.d3(size, shape, result)
}

@JvmName("batchD3sMinusD3s")
@ScopeOp
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D3>): Batch<IOType.D3.Global> {
    val result = Backend.minus(x = value, y = other.value)
    return Batch.d3(size, shape, result)
}
