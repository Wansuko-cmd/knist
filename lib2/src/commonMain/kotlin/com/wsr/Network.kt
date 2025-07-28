package com.wsr

import com.wsr.common.IOType1d

class Network(private val layers: List<Layer<IOType1d>>) {
    fun expect(input: IOType1d): IOType1d =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    fun train(input: IOType1d, label: IOType1d) {
        var delta = { input: IOType1d -> label }
        for (index in layers.lastIndex downTo 0) {
            delta = { input: IOType1d -> layers[index].train(input, delta) }
        }
        delta(input)
    }
}
