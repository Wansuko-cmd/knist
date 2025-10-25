package com.wsr

import com.wsr.converter.input.InputConverter
import com.wsr.process.debug.DebugD1
import com.wsr.process.debug.DebugD2
import com.wsr.process.debug.DebugD3
import kotlinx.serialization.Serializable

@Serializable(with = NetworkSerializer::class)
class Network<I, O : IOType> internal constructor(
    internal val converter: InputConverter,
    internal val layers: List<Layer>,
) {
    private val trainLambda: (List<IOType>, List<IOType>) -> List<IOType> =
        layers
            .reversed()
            .fold({ _, label -> label }) { acc: (List<IOType>, List<IOType>) -> List<IOType>, layer: Layer ->
                { input: List<IOType>, label: List<IOType> ->
                    layer._train(input) { acc(it, label) }
                }
            }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O = expect(input = listOf(input))[0]

    @Suppress("UNCHECKED_CAST")
    fun expect(input: List<I>): List<O> = layers
        .fold(converter._convert(input)) { acc, layer -> layer._expect(acc) } as List<O>

    fun train(input: I, label: O) {
        train(input = listOf(input), label = listOf(label))
    }

    fun train(input: List<I>, label: List<O>) {
        trainLambda(converter._convert(input), label)
    }

    fun toJson(): String = json.encodeToString(
        serializer = NetworkSerializer<I, O>(),
        value = Network(
            converter = converter,
            layers = layers.filter { it !is DebugD1 && it !is DebugD2 && it !is DebugD3 },
        ),
    )

    companion object {
        fun <I : IOType, O : IOType> fromJson(value: String) = json.decodeFromString<Network<I, O>>(
            deserializer = NetworkSerializer(),
            string = value,
        )
    }
}
