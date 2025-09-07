package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
import com.wsr.layers.debug.DebugD1
import com.wsr.layers.debug.DebugD2
import kotlinx.serialization.Serializable


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
        val delta = input.value.zip(label.value)
            .map { (y, t) -> y - t }
            .toMutableList()
        // TODO if文を削除
        return when (input) {
            is IOType.D1 -> IOType.d1(value = delta)
            is IOType.D2 -> IOType.d2(shape = input.shape, value = delta)
            is IOType.D3 -> IOType.d3(shape = input.shape, value = delta)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O =
        layers.fold<Layer, IOType>(input) { acc, layer -> layer.expect(acc) } as O

    fun train(input: I, label: O) {
        trainLambda(input, label)
    }

    fun toJson() = json.encodeToString(
        serializer = NetworkSerializer(),
        value = Network(layers = layers.filter { it !is DebugD1 && it !is DebugD2 }),
    )

    companion object {
        fun <I : IOType, O : IOType> fromJson(value: String) =
            json.decodeFromString<Network<I, O>>(
                deserializer = NetworkSerializer(),
                string = value,
        )
    }
}
