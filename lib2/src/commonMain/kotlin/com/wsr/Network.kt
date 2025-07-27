package com.wsr

import org.jetbrains.kotlinx.multik.ndarray.data.Dimension
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray

class Network<D : Dimension>(
    private val layers: List<Layer<D>>,
) {
    fun expect(input: NDArray<Double, D>): NDArray<Double, D> =
        layers.fold(input) { acc, layer -> layer.forward(acc) }

    fun train(input: NDArray<Double, D>, label: NDArray<Double, D>) {
        layers.fold(input) { acc, layer -> layer.forward(acc) }
        layers.reversed().fold(label) { acc, layer -> layer.backward(acc) }
    }
}
