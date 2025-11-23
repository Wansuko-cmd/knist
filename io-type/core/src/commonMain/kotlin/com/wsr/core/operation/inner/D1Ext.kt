package com.wsr.core.operation.inner

import com.wsr.BLAS
import com.wsr.core.IOType

infix fun IOType.D1.inner(other: IOType.D1): Float = BLAS.sdot(
    n = value.size,
    x = value,
    incX = 1,
    y = other.value,
    incY = 1,
)
