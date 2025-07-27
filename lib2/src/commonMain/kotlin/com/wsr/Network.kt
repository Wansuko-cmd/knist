package com.wsr

import com.wsr.common.IOType1d

class Network(private val layers: List<Layer<IOType1d>>) {
    fun expect(input: IOType1d): IOType1d =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    fun train(input: IOType1d, label: IOType1d) {
        layers[0].train(input) { o0 ->
            layers[1].train(o0) { o1 ->
                layers[2].train(o1) { o2 ->
                    layers[3].train(o2) { label }
                }
            }
        }
    }
}
