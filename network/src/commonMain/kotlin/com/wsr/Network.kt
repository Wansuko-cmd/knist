package com.wsr

import com.wsr.batch.Batch
import com.wsr.converter.Converter
import com.wsr.core.IOType
import com.wsr.output.Output
import com.wsr.process.Context
import com.wsr.process.Process
import kotlinx.serialization.Serializable
import okio.BufferedSink
import okio.BufferedSource

private typealias TrainLambda = (input: Batch<IOType>, context: Context) -> Batch<IOType>

@Serializable(with = NetworkSerializer::class)
class Network<I, O> internal constructor(
    val inputConverter: Converter,
    val outputConverter: Converter,
    val layers: List<Process>,
    val output: Output,
) {
    private val trainLambda: (TrainLambda) -> TrainLambda = run {
        val initial: (TrainLambda) -> TrainLambda = { it }
        layers.foldRight(initial) { layer: Process, acc: (TrainLambda) -> TrainLambda ->
            { final: TrainLambda ->
                { input: Batch<IOType>, context: Context ->
                    layer._train(input, context) { i -> acc(final)(i, context) }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun expect(input: I): O = expect(input = listOf(input))[0]

    @Suppress("UNCHECKED_CAST")
    fun expect(input: List<I>): List<O> {
        val input = inputConverter._encode(input)
        val context = Context(input = input)
        val output = layers
            .fold(input) { acc, process -> process._expect(acc, context) }
            .let { output._expect(it) }
        return outputConverter._decode(output) as List<O>
    }

    fun train(input: I, label: O) = train(input = listOf(input), label = listOf(label))

    fun train(input: I, label: (O) -> O) = train(input = listOf(input)) { listOf(label(it[0])) }

    fun train(input: List<I>, label: List<O>): Float = _train(input) {
        outputConverter._encode(label)
    }

    fun train(input: List<I>, label: (List<O>) -> List<O>): Float = _train(input) {
        @Suppress("UNCHECKED_CAST")
        val output = outputConverter._decode(it) as List<O>
        val label = label(output)
        outputConverter._encode(label)
    }

    @Suppress("FunctionName")
    private inline fun _train(input: List<I>, crossinline label: (Batch<IOType>) -> Batch<IOType>): Float {
        var loss = 0f
        val output: TrainLambda = { input: Batch<IOType>, context: Context ->
            val output = output._train(input) { label(it) }
            loss = output.loss
            output.delta
        }
        val input = inputConverter._encode(input)
        val context = Context(input = input)
        trainLambda(output).invoke(input, context)
        return loss
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
