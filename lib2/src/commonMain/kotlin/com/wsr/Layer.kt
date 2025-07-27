package com.wsr

import org.jetbrains.kotlinx.multik.ndarray.data.Dimension
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray

interface Layer<D : Dimension> {
    fun forward(input: NDArray<Double, D>): NDArray<Double, D>
    fun backward(delta: NDArray<Double, D>): NDArray<Double, D>
}

interface Layer2<IOType> {
    fun expect(input: IOType): IOType
    fun train(input: IOType, delta: (output: IOType) -> IOType): IOType
}
