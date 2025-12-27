package com.wsr.batch.operation.times

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.operation.times.times

@JvmName("batchD3TimesD3s")
operator fun IOType.D3.times(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.times(
        x = value,
        y = other.value,
        yi = other.size,
        yj = other.step,
        axis = 1,
    )
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchD3sTimesFloat")
operator fun Batch<IOType.D3>.times(other: Float): Batch<IOType.D3> {
    val result = Backend.times(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sTimesD0s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D0>): Batch<IOType.D3> {
    val result = Backend.times(x = value, xi = size, xj = step, y = other.value, axis = 0)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sTimesD1WithAxis")
fun Batch<IOType.D3>.times(other: IOType.D1, axis: Int): Batch<IOType.D3> {
    val result = Backend.times(
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

@JvmName("batchD3sTimesD2")
operator fun Batch<IOType.D3>.times(other: IOType.D2): Batch<IOType.D3> = times(other = other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sTimesD2WithAxis")
fun Batch<IOType.D3>.times(other: IOType.D2, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.times(
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

@JvmName("batchD3sTimesD2s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D2>) = times(other, axis1 = 1, axis2 = 2)

@JvmName("batchD3sTimesD2sWithAxis")
fun Batch<IOType.D3>.times(other: Batch<IOType.D2>, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.times(
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

@JvmName("batchD3sTimesD3")
operator fun Batch<IOType.D3>.times(other: IOType.D3): Batch<IOType.D3> {
    val result = Backend.times(
        x = value,
        xi = size,
        xj = step,
        y = other.value,
        axis = 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD3sTimesD3s")
operator fun Batch<IOType.D3>.times(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.times(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}
