package com.wsr.core.operation.inner

import com.wsr.BLAS
import com.wsr.core.IOType

infix fun IOType.D1.inner(other: IOType.D1): Float = BLAS.sdot2(
    x = value,
    y = other.value,
)
