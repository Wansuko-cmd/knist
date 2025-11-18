package com.wsr

import com.wsr.converter.Converter
import com.wsr.layer.Layer
import com.wsr.output.Output
import kotlinx.serialization.Serializable
import okio.BufferedSink
import okio.BufferedSource

@Serializable(with = NetworkSerializer::class)
class Network<I, O> internal constructor(
    val inputConverter: Converter,
    val outputConverter: Converter,
    val layers: List<Layer>,
    val output: Output,
) {
    private val trainLambda: (List<IOType>, List<IOType>) -> List<IOType> =
        layers
            .reversed()
            .fold({ acc, label ->  output._train(acc, label) }) { acc: (List<IOType>, List<IOType>) -> List<IOType>, layer: Layer ->
                { input: List<IOType>, label: List<IOType> ->
                    layer._train(input) { acc(it, label) }
                }
            }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O = expect(input = listOf(input))[0]

    @Suppress("UNCHECKED_CAST")
    fun expect(input: List<I>): List<O> = layers
        .fold(inputConverter._encode(input)) { acc, process -> process._expect(acc) }
        .let { output._expect(it) }
        .let { outputConverter._decode(it) } as List<O>

    fun train(input: I, label: O) {
        train(input = listOf(input), label = listOf(label))
    }

    fun train(input: List<I>, label: List<O>) {
        trainLambda(inputConverter._encode(input), outputConverter._encode(label))
    }

    fun toJson(): String = NetworkSerializer.encodeToString(this)

    fun toJson(sink: BufferedSink) {
        NetworkSerializer.encodeToBufferedSink(
            value = this,
            sink = sink,
        )
    }

    companion object {
        fun <I, O> fromJson(value: String) = NetworkSerializer.decodeFromString<I, O>(value)

        fun <I, O> fromJson(source: BufferedSource) = NetworkSerializer.decodeFromBufferedSource<I, O>(source)
    }
}
