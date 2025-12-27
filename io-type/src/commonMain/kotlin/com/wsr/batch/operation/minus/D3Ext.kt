package com.wsr.batch.operation.minus

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD3sMinusFloat")
operator fun Batch<IOType.D3>.minus(other: Float): Batch<IOType.D3> {
    val result = Backend.minus(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sMinusD0s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D0>): Batch<IOType.D3> {
    val result = Backend.minus(x = value, xi = size, xj = step, y = other.value, axis = 0)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sMinusD1WithAxis")
fun Batch<IOType.D3>.minus(other: IOType.D1, axis: Int): Batch<IOType.D3> {
    val result = Backend.minus(
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

@JvmName("batchD3sMinusD2")
operator fun Batch<IOType.D3>.minus(other: IOType.D2): Batch<IOType.D3> = minus(other = other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sMinusD2WithAxis")
fun Batch<IOType.D3>.minus(other: IOType.D2, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.minus(
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

@JvmName("batchD3sMinusD2s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D2>) = minus(other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sMinusD2sWithAxis")
fun Batch<IOType.D3>.minus(other: Batch<IOType.D2>, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.minus(
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

@JvmName("batchD3sMinusD3")
operator fun Batch<IOType.D3>.minus(other: IOType.D3): Batch<IOType.D3> {
    val result = Backend.minus(
        x = value,
        xi = size,
        xj = step,
        y = other.value,
        axis = 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sMinusD3s")
operator fun Batch<IOType.D3>.minus(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.minus(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}
