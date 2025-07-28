package com.wsr

import com.wsr.common.IOType1d

class Network2(private val layers: List<Layer<IOType1d>>) {
    fun expect(input: IOType1d): IOType1d =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    private val trainLambda: (IOType1d, IOType1d) -> IOType1d = layers
        .reversed()
        .fold(::output) { acc: (IOType1d, IOType1d) -> IOType1d, layer: Layer<IOType1d> ->
            { input: IOType1d, label: IOType1d ->
                layer.train(input) { acc(it, label) }
            }
        }

    private fun output(input: IOType1d, label: IOType1d) =
        Array(input.size) { input[it] - label[it] }

    fun train(input: IOType1d, label: IOType1d) {
        trainLambda(input, label)
    }
}
