package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
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
        val input = IOType.D1(input.value)
        val label = IOType.D1(label.value)
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
}
