package com.wsr

import com.wsr.common.IOTypeD1
import com.wsr.layers.Layer
import kotlin.random.Random

class Network private constructor(private val layers: List<Layer>) {
    private val trainLambda: (IOTypeD1, IOTypeD1) -> IOTypeD1 by lazy {
        layers
            .reversed()
            .fold(::output) { acc: (IOTypeD1, IOTypeD1) -> IOTypeD1, layer: Layer ->
                { input: IOTypeD1, label: IOTypeD1 ->
                    layer.train(input) { acc(it, label) }
                }
            }
    }

    private fun output(input: IOTypeD1, label: IOTypeD1) =
        Array(input.size) { input[it] - label[it] }

    fun expect(input: IOTypeD1): IOTypeD1 =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    fun train(input: IOTypeD1, label: IOTypeD1) {
        trainLambda(input, label)
    }

    @ConsistentCopyVisibility
    data class Builder private constructor(
        val numOfInput: Int,
        val rate: Double,
        val random: Random,
        private val layers: List<Layer>,
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

        fun addLayer(layer: Layer) =
            copy(numOfInput = layer.numOfOutput, layers = layers + layer)

        fun build() = Network(layers)
    }
}
