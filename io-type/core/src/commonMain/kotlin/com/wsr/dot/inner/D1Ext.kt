package com.wsr.dot.inner

import com.wsr.BLAS
import com.wsr.IOType

infix fun IOType.D1.inner(other: IOType.D1): Float = BLAS.sdot(
    n = value.size,
    x = value,
    incX = 1,
    y = other.value,
    incY = 1,
)

@JvmName("innerToD1s")
infix fun IOType.D1.inner(other: List<IOType.D1>) = List(other.size) { this inner other[it] }
