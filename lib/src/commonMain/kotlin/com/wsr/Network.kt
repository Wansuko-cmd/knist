package com.wsr

import com.wsr.layers.debug.DebugD1
import com.wsr.layers.debug.DebugD2
import kotlinx.serialization.Serializable


@Serializable(with = NetworkSerializer::class)
class Network<I : IOType, O : IOType> internal constructor(
    internal val layers: List<Layer>,
) {
    private val trainLambda: (List<IOType>, List<IOType>) -> List<IOType> = layers
        .reversed()
        .fold({ _, label -> label }) { acc: (List<IOType>, List<IOType>) -> List<IOType>, layer: Layer ->
            { input: List<IOType>, label: List<IOType> ->
                layer._train(input) { acc(it, label) }
            }
        }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O = expect(input = listOf(input))[0]

    @Suppress("UNCHECKED_CAST")
    fun expect(input: List<I>): List<O> =
        layers.fold<Layer, List<IOType>>(input) { acc, layer -> layer._expect(acc) } as List<O>

    fun train(input: I, label: O) {
        train(input = listOf(input), label = listOf(label))
    }

    fun train(input: List<I>, label: List<O>) {
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
