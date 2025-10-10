package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

operator fun Double.times(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    BLAS.dscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d1(result)
}

@JvmName("timesToD1s")
operator fun Double.times(other: List<IOType.D1>) = other.map { this * it }

operator fun Double.times(other: IOType.D2): IOType.D2 {
    val result = other.value.copyOf()
    BLAS.dscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d2(other.shape, result)
}

@JvmName("timesToD2s")
operator fun Double.times(other: List<IOType.D2>) = other.map { this * it }

operator fun Double.times(other: IOType.D3): IOType.D3 {
    val result = other.value.copyOf()
    BLAS.dscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d3(other.shape, result)
}
