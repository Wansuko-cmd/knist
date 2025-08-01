package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable(with = NetworkSerializer::class)
class Network<I : IOType, O : IOType> internal constructor(internal val layers: List<Layer>) {
    private val trainLambda: (IOType, IOType) -> IOType by lazy {
        layers
            .reversed()
            .fold(::output) { acc: (IOType, IOType) -> IOType, layer: Layer ->
                { input: IOType, label: IOType ->
                    layer.train(input) { acc(it, label) }
                }
            }
    }

    private fun output(input: IOType, label: IOType): IOType {
        val input = input.toD1()
        val label = label.toD1()
        return IOType.D1(input.shape[0]) { input[it] - label[it] }
    }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O =
        layers.fold<Layer, IOType>(input) { acc, layer -> layer.expect(acc) } as O

    fun train(input: I, label: O) {
        trainLambda(input, label)
    }

    fun toJson() = json.encodeToString(NetworkSerializer(), this)

    companion object {
        fun <I : IOType, O : IOType> fromJson(value: String) =
            json.decodeFromString<Network<I, O>>(NetworkSerializer(), value,
        )
    }

    @ConsistentCopyVisibility
    data class Builder private constructor(
        val numOfInput: Int,
        val rate: Double,
        val random: Random,
        private val layers: List<Layer.D1>,
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

        fun addLayer(layer: Layer.D1) =
            copy(numOfInput = layer.numOfOutput, layers = layers + layer)

        fun build() = Network<IOType.D1, IOType.D1>(layers)
    }
}
