package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

operator fun IOType.D1.div(other: Double): IOType.D1 {
    val result = this.value.copyOf()
    BLAS.dscal(n = result.size, alpha = 1.0 / other, x = result, incX = 1)
    return IOType.d1(result)
}

operator fun Double.div(other: IOType.D2) = IOType.d2(other.shape) { x, y -> this / other[x, y] }

operator fun Double.div(other: IOType.D3) = IOType.d3(other.shape) { x, y, z ->
    this /
        other[x, y, z]
}
