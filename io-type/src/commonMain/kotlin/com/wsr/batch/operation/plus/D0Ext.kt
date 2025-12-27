package com.wsr.batch.operation.plus

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.core.IOType

@JvmName("batchFloatPlusD0s")
operator fun Float.plus(other: Batch<IOType.D0>): Batch<IOType.D0> {
    val result = Backend.plus(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatPlusD1s")
operator fun Float.plus(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.plus(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatPlusD2s")
operator fun Float.plus(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.plus(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatPlusD3s")
operator fun Float.plus(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.plus(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchD0sPlusFloat")
operator fun Batch<IOType.D0>.plus(other: Float): Batch<IOType.D0> {
    val result = Backend.plus(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD0sPlusD0s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D0>): Batch<IOType.D0> {
    val result = Backend.plus(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD0sPlusD1s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.plus(x = value, y = other.value, yi = other.size, yj = other.step, axis = 0)
    return Batch(size = size, shape = other.shape, value = result)
}

@JvmName("batchD0sPlusD2s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.plus(
        x = value,
        y = other.value,
        yi = size,
        yj = other.step,
        axis = 0,
    )
    return Batch(size = size, shape = other.shape, value = result)
}

@JvmName("batchD0sPlusD3s")
operator fun Batch<IOType.D0>.plus(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.plus(
        x = value,
        y = other.value,
        yi = size,
        yj = other.step,
        axis = 0,
    )
    return Batch(size = size, shape = other.shape, value = result)
}
