package com.wsr

expect object BLAS : IBLAS

interface IBLAS {
    val isNative: Boolean get() = false

    fun ddot(
        n: Int,
        x: DoubleArray,
        incX: Int,
        y: DoubleArray,
        incY: Int,
    ): Double {
        var result = 0.0
        var xi = 0
        var yi = 0

        repeat(n) {
            result += x[xi] * y[yi]
            xi += incX
            yi += incY
        }

        return result
    }
}
