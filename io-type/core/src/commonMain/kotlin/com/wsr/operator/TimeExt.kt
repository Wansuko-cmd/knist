package com.wsr.operator

import com.wsr.BLAS
import com.wsr.IOType

/**
 * Float
 */
@JvmName("timesFloatToD0")
operator fun Float.times(other: IOType.D0) = IOType.d0(this * other.get())

operator fun Float.times(other: IOType.D1): IOType.D1 {
    val result = other.value.copyOf()
    BLAS.sscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d1(result)
}

@JvmName("timesFloatToD1s")
operator fun Float.times(other: List<IOType.D1>) = other.map { this * it }

operator fun Float.times(other: IOType.D2): IOType.D2 {
    val result = other.value.copyOf()
    BLAS.sscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d2(other.shape, result)
}

@JvmName("timesFloatToD2s")
operator fun Float.times(other: List<IOType.D2>) = other.map { this * it }

operator fun Float.times(other: IOType.D3): IOType.D3 {
    val result = other.value.copyOf()
    BLAS.sscal(n = result.size, alpha = this, x = result, incX = 1)
    return IOType.d3(other.shape, result)
}

@JvmName("timesD0ToFloat")
operator fun IOType.D0.times(other: Float) = IOType.d0(get() * other)

@JvmName("timesD0ToD0")
operator fun IOType.D0.times(other: IOType.D0) = IOType.d0(get() * other.get())

@JvmName("timesD0ToD1")
operator fun IOType.D0.times(other: IOType.D1) = this.get() * other

@JvmName("timesD0ToD2")
operator fun IOType.D0.times(other: IOType.D2) = this.get() * other

@JvmName("timesD0ToD3")
operator fun IOType.D0.times(other: IOType.D3) = this.get() * other

/**
 * IOType.D1
 */
operator fun IOType.D1.times(other: Float): IOType.D1 {
    val result = value.copyOf()
    BLAS.sscal(n = result.size, alpha = other, x = result, incX = 1)
    return IOType.d1(result)
}

operator fun IOType.D1.times(other: IOType.D1): IOType.D1 {
    val result = value.copyOf()
    for (i in result.indices) {
        result[i] *= other.value[i]
    }
    return IOType.d1(result)
}

// operator fun IOType.D1.times(other: IOType.D2): IOType.D2 {
//    val result = other.value.copyOf()
//    for (i in result.indices) {
//        result[i] *= value[i]
//    }
//    return IOType.d1(result)
// }

/**
 * IOType.D2
 */
operator fun IOType.D2.times(other: Float): IOType.D2 {
    val result = value.copyOf()
    BLAS.sscal(n = result.size, alpha = other, x = result, incX = 1)
    return IOType.d2(shape, result)
}

operator fun IOType.D2.times(other: IOType.D2): IOType.D2 {
    val result = value.copyOf()
    for (i in result.indices) result[i] *= other.value[i]
    return IOType.d2(shape, result)
}

/**
 * IOType.D3
 */
operator fun IOType.D3.times(other: Float): IOType.D3 {
    val result = value.copyOf()
    BLAS.sscal(n = result.size, alpha = other, x = result, incX = 1)
    return IOType.d3(shape, result)
}

operator fun IOType.D3.times(other: IOType.D3): IOType.D3 {
    val result = value.copyOf()
    for (i in result.indices) result[i] *= other.value[i]
    return IOType.d3(shape, result)
}
