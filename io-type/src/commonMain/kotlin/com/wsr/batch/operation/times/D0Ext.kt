package com.wsr.batch.operation.times

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.mapValue
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.times.times

@JvmName("batchFloatTimesD0s")
operator fun Float.times(other: Batch<IOType.D0>): Batch<IOType.D0> {
    val result = Backend.times(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatTimesD1s")
operator fun Float.times(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.times(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatTimesD2s")
operator fun Float.times(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.times(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchFloatTimesD3s")
operator fun Float.times(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.times(x = this, y = other.value)
    return Batch(size = other.size, shape = other.shape, value = result)
}

@JvmName("batchD0sTimesFloat")
operator fun Batch<IOType.D0>.times(other: Float): Batch<IOType.D0> {
    val result = Backend.times(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD0sTimesD0s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D0>): Batch<IOType.D0> {
    val result = Backend.times(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD0sTimesD1s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D1>): Batch<IOType.D1> {
    val result = Backend.times(x = value, y = other.value, yi = other.size, yj = other.step, axis = 0)
    return Batch(size = size, shape = other.shape, value = result)
}

@JvmName("batchD0sTimesD2s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.times(
        x = value,
        y = other.value,
        yi = size,
        yj = other.step,
        axis = 0,
    )
    return Batch(size = size, shape = other.shape, value = result)
}

@JvmName("batchD0sTimesD3s")
operator fun Batch<IOType.D0>.times(other: Batch<IOType.D3>): Batch<IOType.D3> {
    val result = Backend.times(
        x = value,
        y = other.value,
        yi = size,
        yj = other.step,
        axis = 0,
    )
    return Batch(size = size, shape = other.shape, value = result)
}
