package com.wsr.batch.operation.div

import com.wsr.Backend
import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.operation.div.div

@JvmName("batchD2sDivFloat")
operator fun Batch<IOType.D2>.div(other: Float): Batch<IOType.D2> {
    val result = Backend.div(x = value, y = other)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sDivD0s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D0>): Batch<IOType.D2> {
    val result = Backend.div(x = value, xi = size, xj = step, y = other.value, axis = 0)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sDivD1WithAxis")
fun Batch<IOType.D2>.div(other: IOType.D1, axis: Int): Batch<IOType.D2> {
    val result = Backend.div(x = value, xi = size, xj = shape[0], xk = shape[1], y = other.value, axis = axis + 1)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sDivD1sWithAxis")
fun Batch<IOType.D2>.div(other: Batch<IOType.D1>, axis: Int): Batch<IOType.D2> {
    val result = Backend.div(
        x = value,
        xi = size,
        xj = shape[0],
        xk = shape[1],
        y = other.value,
        yi = other.size,
        yj = other.shape[0],
        axis1 = 0,
        axis2 = axis + 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sDivD2")
operator fun Batch<IOType.D2>.div(other: IOType.D2): Batch<IOType.D2> {
    val result = Backend.div(
        x = value,
        xi = size,
        xj = step,
        y = other.value,
        axis = 1,
    )
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sDivD2s")
operator fun Batch<IOType.D2>.div(other: Batch<IOType.D2>): Batch<IOType.D2> {
    val result = Backend.div(x = value, y = other.value)
    return Batch(size = size, shape = shape, value = result)
}

@JvmName("batchD2sDivD3sWithAxis")
fun Batch<IOType.D2>.div(other: Batch<IOType.D3>, axis1: Int, axis2: Int): Batch<IOType.D3> {
    val result = Backend.div(
        x = value,
        xi = size,
        xj = shape[0],
        xk = shape[1],
        y = other.value,
        yi = other.size,
        yj = other.shape[0],
        yk = other.shape[1],
        yl = other.shape[2],
        axis1 = 0,
        axis2 = axis1 + 1,
        axis3 = axis2 + 1,
    )
    return Batch(size = other.size, shape = other.shape, value = result)
}
