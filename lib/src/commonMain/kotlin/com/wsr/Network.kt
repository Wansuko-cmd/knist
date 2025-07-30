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
class Network private constructor(private val layers: List<Layer>) {
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
        val input = input as IOType.D1
        val label = label as IOType.D1
        return IOType.D1(input.size) { input[it] - label[it] }
    }

    fun expect(input: IOType.D1): IOType.D1 =
        layers.fold(input) { acc, layer -> layer.expect(acc) as IOType.D1 }

    fun train(input: IOType.D1, label: IOType.D1) {
        trainLambda(input, label)
    }

    fun toJson() = json.encodeToString(this)

    companion object {
        fun fromJson(value: String) = json.decodeFromString<Network>(value)
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

        fun build() = Network(layers)
    }
}
