package com.wsr.batch.operation.div

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchFloatDivD0s")
operator fun Float.div(other: Batch<IOType.D0>): Batch<IOType.D0> {
    val result = Backend.div(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatDivD1s")
operator fun Float.div(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.div(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatDivD2s")
operator fun Float.div(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.div(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatDivD3s")
operator fun Float.div(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.div(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchD0sDivFloat")
operator fun Batch<IOType.D0>.div(other: Float): Batch<IOType.D0> {
    val result = Backend.div(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD0sDivD0s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D0>): Batch<IOType.D0> {
    val result = Backend.div(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD0sDivD1s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.div(x = value, y = other.value, yi = other.size, yj = other.step, axis = 0)
    return Batch(size = size, shape = other.shape, value = result)
}

@JvmName("batchD0sDivD2s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.div(
        x = value,
        y = other.value,
        yi = size,
        yj = other.step,
        axis = 0,
    )
    return Batch(size = size, shape = other.shape, value = result)
}

@JvmName("batchD0sDivD3s")
operator fun Batch<IOType.D0>.div(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.div(
        x = value,
        y = other.value,
        yi = size,
        yj = other.step,
        axis = 0,
    )
    return Batch(size = size, shape = other.shape, value = result)
}
