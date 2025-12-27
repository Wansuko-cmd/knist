package com.wsr.batch.operation.plus

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD3sPlusFloat")
operator fun Batch<IOType.D3>.plus(other: Float): Batch<IOType.D3> {
    val result = Backend.plus(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPlusD0s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D0>): Batch<IOType.D3> {
    val result = Backend.plus(x = value, xi = size, xj = step, y = other.value, axis = 0)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPlusD1WithAxis")
fun Batch<IOType.D3>.plus(other: IOType.D1, axis: Int): Batch<IOType.D3> {
    val result = Backend.plus(
        x = value,
        xi = size,
        xj = shape[0],
        xk = shape[1],
        xl = shape[2],
        y = other.value,
        axis = axis + 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPlusD2")
operator fun Batch<IOType.D3>.plus(other: IOType.D2): Batch<IOType.D3> = plus(other = other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sPlusD2WithAxis")
fun Batch<IOType.D3>.plus(other: IOType.D2, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.plus(
        x = value,
        xi = size,
        xj = shape[0],
        xk = shape[1],
        xl = shape[2],
        y = other.value,
        yi = other.shape[0],
        yj = other.shape[1],
        axis1 = axis1 + 1,
        axis2 = axis2 + 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPlusD2s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D2>) = plus(other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sPlusD2sWithAxis")
fun Batch<IOType.D3>.plus(other: Batch<IOType.D2>, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.plus(
        x = value,
        xi = size,
        xj = shape[0],
        xk = shape[1],
        xl = shape[2],
        y = other.value,
        yi = other.size,
        yj = other.shape[0],
        yk = other.shape[1],
        axis1 = 0,
        axis2 = axis1 + 1,
        axis3 = axis2 + 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPlusD3")
operator fun Batch<IOType.D3>.plus(other: IOType.D3): Batch<IOType.D3> {
    val result = Backend.plus(
        x = value,
        xi = size,
        xj = step,
        y = other.value,
        axis = 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sPlusD3s")
operator fun Batch<IOType.D3>.plus(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.plus(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}
