package com.wsr.batch.operation.plus

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchD1sPlusFloat")
operator fun Batch<IOType.D1>.plus(other: Float): Batch<IOType.D1> {
    val result = Backend.plus(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sPlusD0s")
operator fun Batch<IOType.D1>.plus(other: Batch<IOType.D0>): Batch<IOType.D1> {
    val result = Backend.plus(
        x = value,
        xi = size,
        xj = step,
        y = other.value,
        axis = 0,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sPlusD1")
operator fun Batch<IOType.D1>.plus(other: IOType.D1): Batch<IOType.D1> {
    val result = Backend.plus(x = value, xi = size, xj = step, y = other.value, axis = 1)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sPlusD1s")
operator fun Batch<IOType.D1>.plus(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.plus(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD1sPlusD2sWithAxis")
fun Batch<IOType.D1>.plus(other: Batch<IOType.D2>, axis: Int): Batch<IOType.D2> {
    val result = Backend.plus(
        x = value,
        xi = size,
        xj = shape[0],
        y = other.value,
        yi = other.size,
        yj = other.shape[0],
        yk = other.shape[1],
        axis1 = 0,
        axis2 = axis + 1,
    )
    return Batch(size = size, shape = shape, value = result)
}
