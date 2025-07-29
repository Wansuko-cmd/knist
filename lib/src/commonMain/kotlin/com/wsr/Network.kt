package com.wsr

import com.wsr.common.IOType1d
import com.wsr.layer.Layer
import kotlin.random.Random

class Network2(private val layers: List<Layer<IOType1d>>) {
    private val trainLambda: (IOType1d, IOType1d) -> IOType1d = layers
        .reversed()
        .fold(::output) { acc: (IOType1d, IOType1d) -> IOType1d, layer: Layer<IOType1d> ->
            { input: IOType1d, label: IOType1d ->
                layer.train(input) { acc(it, label) }
            }
        }

    private fun output(input: IOType1d, label: IOType1d) =
        Array(input.size) { input[it] - label[it] }

    fun expect(input: IOType1d): IOType1d =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    fun train(input: IOType1d, label: IOType1d) {
        trainLambda(input, label)
    }

    @ConsistentCopyVisibility
    data class Builder private constructor(
        val numOfInput: Int,
        val rate: Double,
        val random: Random,
        private val layers: List<Layer<IOType1d>>,
    ) {
        constructor(
            numOfInput: Int,
            rate: Double,
            seed: Int? = null,
        ) : this(
            numOfInput = numOfInput,
            rate = rate,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )

        fun addLayer(layer: Layer<IOType1d>) =
            copy(numOfInput = layer.numOfOutput, layers = layers + layer)

        fun build() = Network2(layers)
    }
}
