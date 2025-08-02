package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
import com.wsr.layers.reshape.ReshapeD2ToD1
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

sealed interface NetworkBuilder<I : IOType, O : IOType> {
    val layers: List<Layer>
    val rate: Double
    val random: Random

    fun build() = Network<I, O>(layers)

    data class D1<I : IOType>(
        val numOfInput: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D1> {
        fun addLayer(layer: Layer.D1): D1<I> = copy(
            layers = layers + layer,
            numOfInput = layer.numOfOutput,
        )
    }

    data class D2<I : IOType>(
        val x: Int,
        val y: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addLayer(layer: Layer.D2): D2<I> = copy(
            layers = layers + layer,
            x = layer.outputShape[0],
            y = layer.outputShape[1],
        )

        fun reshapeD1(): D1<I> {
            val numOfInput = x * y
            return D1(
                numOfInput = numOfInput,
                layers = layers + ReshapeD2ToD1(listOf(x, y), listOf(numOfInput)),
                rate = rate,
                random = random,
            )
        }
    }

    companion object {
        fun inputD1(
            numOfInput: Int,
            rate: Double,
            seed: Int? = null,
        ) = D1<IOType.D1>(
            numOfInput = numOfInput,
            rate = rate,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )

        fun inputD2(
            x: Int,
            y: Int,
            rate: Double,
            seed: Int? = null,
        ) = D2<IOType.D2>(
            x = x,
            y = y,
            rate = rate,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )
    }
}
