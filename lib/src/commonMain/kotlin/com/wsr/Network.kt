package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
import com.wsr.layers.debug.DebugD1
import com.wsr.layers.debug.DebugD2
import kotlinx.serialization.Serializable


@Serializable(with = NetworkSerializer::class)
class Network<I : IOType, O : IOType> internal constructor(internal val layers: List<Layer>) {
    private val trainLambda: (List<IOType>, List<IOType>) -> List<IOType> by lazy {
        layers
            .reversed()
            .fold(::output) { acc: (List<IOType>, List<IOType>) -> List<IOType>, layer: Layer ->
                { input: List<IOType>, label: List<IOType> ->
                    layer._train(input) { acc(it, label) }
                }
            }
    }

    private fun output(input: List<IOType>, label: List<IOType>): List<IOType> = List(input.size) { index ->
        val delta = input[index].value.zip(label[index].value)
            .map { (y, t) -> y - t }
            .toMutableList()
        // TODO if文を削除
        when (input[index]) {
            is IOType.D1 -> IOType.d1(value = delta)
            is IOType.D2 -> IOType.d2(shape = input[index].shape, value = delta)
            is IOType.D3 -> IOType.d3(shape = input[index].shape, value = delta)
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
