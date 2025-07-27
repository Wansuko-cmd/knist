package com.wsr

import org.jetbrains.kotlinx.multik.ndarray.data.Dimension
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray

interface Layer<D : Dimension> {
    fun forward(input: NDArray<Double, D>): NDArray<Double, D>
    fun backward(delta: NDArray<Double, D>): NDArray<Double, D>
}
