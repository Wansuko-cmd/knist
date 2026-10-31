package com.wsr.knist.batch.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d3
import com.wsr.knist.batch.d4
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.batch.k
import com.wsr.knist.batch.l
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import kotlin.jvm.JvmName

@JvmName("batchD4sReshapeToD3")
fun Batch<IOType.D4>.reshapeToD3(i: Int, j: Int, k: Int): Batch<IOType.D3> = Batch.d3(size, i, j, k, value)

@ScopeOp
fun Batch<IOType.D4>.concat(other: Batch<IOType.D4>, axis: Int): Batch<IOType.D4.Global> = when (axis) {
    0 -> {
        val newI = i + other.i
        val result = DataBuffer.create(size * newI * j * k * l)
        Backend.copyInto(value, result, yi = size, yj = newI, yk = j * k * l, axis = 1, indices = 0 until i)
        Backend.copyInto(other.value, result, yi = size, yj = newI, yk = j * k * l, axis = 1, indices = i until newI)
        Batch.d4(size, newI, j, k, l, result)
    }

    1 -> {
        val newJ = j + other.j
        val result = DataBuffer.create(size * i * newJ * k * l)
        Backend.copyInto(value, result, yi = size * i, yj = newJ, yk = k * l, axis = 1, indices = 0 until j)
        Backend.copyInto(other.value, result, yi = size * i, yj = newJ, yk = k * l, axis = 1, indices = j until newJ)
        Batch.d4(size, i, newJ, k, l, result)
    }

    2 -> {
        val newK = k + other.k
        val result = DataBuffer.create(size * i * j * newK * l)
        Backend.copyInto(value, result, yi = size * i * j, yj = newK, yk = l, axis = 1, indices = 0 until k)
        Backend.copyInto(other.value, result, yi = size * i * j, yj = newK, yk = l, axis = 1, indices = k until newK)
        Batch.d4(size, i, j, newK, l, result)
    }

    3 -> {
        val newL = l + other.l
        val result = DataBuffer.create(size * i * j * k * newL)
        Backend.copyInto(value, result, yi = size * i * j * k, yj = newL, axis = 1, indices = 0 until l)
        Backend.copyInto(other.value, result, yi = size * i * j * k, yj = newL, axis = 1, indices = l until newL)
        Batch.d4(size, i, j, k, newL, result)
    }

    else -> throw IllegalArgumentException("Batch<IOType.D4>.concat axis is $axis, not 0, 1, 2 or 3.")
}

@ScopeOp
fun Batch<IOType.D4>.transpose(axisI: Int, axisJ: Int, axisK: Int, axisL: Int): Batch<IOType.D4.Global> {
    val result = Backend.transpose(
        x = value,
        xi = size,
        xj = i,
        xk = j,
        xl = k,
        xm = l,
        axisI = 0,
        axisJ = axisI + 1,
        axisK = axisJ + 1,
        axisL = axisK + 1,
        axisM = axisL + 1,
    )
    return Batch.d4(size, shape[axisI], shape[axisJ], shape[axisK], shape[axisL], result)
}
