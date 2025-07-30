package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
import com.wsr.layers.affine.AffineD1
import com.wsr.layers.bias.BiasD1
import com.wsr.layers.function.ReluD1
import com.wsr.layers.function.SigmoidD1
import com.wsr.layers.function.SoftmaxD1
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.random.Random

private val json = Json {
    serializersModule = SerializersModule {
        polymorphic(Layer.D1::class) {
            subclass(AffineD1::class)
            subclass(BiasD1::class)
            subclass(ReluD1::class)
            subclass(SigmoidD1::class)
            subclass(SoftmaxD1::class)
        }
    }
}

@Serializable
class Network<I: IOType, O: IOType> private constructor(private val layers: List<Layer>) {
    private val trainLambda: (IOType, IOType) -> IOType by lazy {
        layers
            .reversed()
            .fold(::output) { acc: (IOType, IOType) -> IOType, layer: Layer ->
                { input: IOType, label: IOType ->
                    layer.train(input) { acc(it, label) }
                }
            }
    }

    private fun output(input: IOType, label: IOType): IOType = when(input) {
        is IOType.D1 -> {
            val label = label as IOType.D1
            IOType.D1(input.size) { input[it] - label[it] }
        }
        is IOType.D2 -> TODO()
    }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O =
        layers.fold<Layer, IOType>(input) { acc, layer -> layer.expect(acc) } as O

    fun train(input: I, label: O) {
        trainLambda(input, label)
    }

    fun toJson() = json.encodeToString(this)

    companion object {
        fun <I: IOType, O: IOType> fromJson(value: String) = json.decodeFromString<Network<I, O>>(value)
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
