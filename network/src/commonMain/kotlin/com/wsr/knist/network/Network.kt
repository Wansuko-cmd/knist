@file:OptIn(ExperimentalSerializationApi::class)

package com.wsr.knist.network

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.network.converter.Converter
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.output.Output
import kotlin.jvm.JvmName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import okio.BufferedSource

interface Network {
    interface Src1 {
        class Sink1<I, O>(val source: Graph.Source<I>, override val graph: List<Graph.Node>, val sink: Graph.Sink<O>, override val optimizer: Optimizer, override val initializer: WeightInitializer) :
            GraphNetwork<Sink1<I, O>>() {
            override val sources: List<Graph.Source<*>> = listOf(source)
            override val sinks: List<Graph.Sink<*>> = listOf(sink)

            suspend fun expect(input: I, dispatcher: CoroutineDispatcher = Dispatchers.Default): O = _expect(
                inputs = listOf(source.converter._encode(input)),
                dispatcher = dispatcher,
            ) { outputs -> sink.converter._decode(outputs[0]) }

            suspend fun loss(input: I, label: O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> { sink.converter._encode(label) }
                return _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend inline fun loss(input: I, crossinline label: (O) -> O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> {
                    val output = sink.converter._decode(it)
                    sink.converter._encode(label(output))
                }
                return _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend fun train(input: I, label: O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> { sink.converter._encode(label) }
                return _train(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend inline fun train(input: I, crossinline label: (O) -> O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> {
                    val output = sink.converter._decode(it)
                    sink.converter._encode(label(output))
                }
                return _train(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            fun <I2> replaceSource(converter: Converter<I2>): Sink1<I2, O> {
                val copy = clone()
                return Sink1(
                    source = Graph.Source(id = copy.source.id, converter = converter),
                    graph = copy.graph,
                    sink = copy.sink,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSinkD1")
            fun <T : Output.D1, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source = copy.source,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSinkD2")
            fun <T : Output.D2, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source = copy.source,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSinkD3")
            fun <T : Output.D3, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source = copy.source,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink1<I, O> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink1<I, O>> = serializer<I, O>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I, O> build(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink1<I, O> {
                    check(sources.size == 1) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 1) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink1(
                        source = sources[0] as Graph.Source<I>,
                        graph = graph,
                        sink = sinks[0] as Graph.Sink<O>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I, O> serializer(): GraphNetworkSerializer<Sink1<I, O>> = GraphNetworkSerializer(::build)

                fun <I, O> fromJson(value: String): Sink1<I, O> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I, O> fromJson(source: BufferedSource): Sink1<I, O> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I, O> fromCbor(bytes: ByteArray): Sink1<I, O> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I, O> fromCbor(source: BufferedSource): Sink1<I, O> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }

        class Sink2<I, O1, O2>(
            val source: Graph.Source<I>,
            override val graph: List<Graph.Node>,
            val sink1: Graph.Sink<O1>,
            val sink2: Graph.Sink<O2>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink2<I, O1, O2>>() {
            override val sources: List<Graph.Source<*>> = listOf(source)
            override val sinks: List<Graph.Sink<*>> = listOf(sink1, sink2)

            suspend fun expect(input: I, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<O1, O2> = _expect(
                inputs = listOf(source.converter._encode(input)),
                dispatcher = dispatcher,
            ) { outputs ->
                sink1.converter._decode(outputs[0]) to sink2.converter._decode(outputs[1])
            }

            suspend fun loss(input: I, label1: O1, label2: O2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                )
                val (loss1, loss2) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend inline fun loss(
                input: I,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                )
                val (loss1, loss2) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend fun train(input: I, label1: O1, label2: O2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                )
                val (loss1, loss2) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend inline fun train(
                input: I,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                )
                val (loss1, loss2) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            fun <I2> replaceSource(converter: Converter<I2>): Sink2<I2, O1, O2> {
                val copy = clone()
                return Sink2(
                    source = Graph.Source(id = copy.source.id, converter = converter),
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSink1D1")
            fun <T : Output.D1, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D1")
            fun <T : Output.D1, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D2")
            fun <T : Output.D2, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D3")
            fun <T : Output.D3, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D2")
            fun <T : Output.D2, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D3")
            fun <T : Output.D3, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink2<I, O1, O2> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink2<I, O1, O2>> = serializer<I, O1, O2>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I, O1, O2> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink2<I, O1, O2> {
                    check(sources.size == 1) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 2) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink2(
                        source = sources[0] as Graph.Source<I>,
                        graph = graph,
                        sink1 = sinks[0] as Graph.Sink<O1>,
                        sink2 = sinks[1] as Graph.Sink<O2>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I, O1, O2> serializer(): GraphNetworkSerializer<Sink2<I, O1, O2>> = GraphNetworkSerializer(::build)

                fun <I, O1, O2> fromJson(value: String): Sink2<I, O1, O2> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I, O1, O2> fromJson(source: BufferedSource): Sink2<I, O1, O2> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I, O1, O2> fromCbor(bytes: ByteArray): Sink2<I, O1, O2> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I, O1, O2> fromCbor(source: BufferedSource): Sink2<I, O1, O2> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }

        class Sink3<I, O1, O2, O3>(
            val source: Graph.Source<I>,
            override val graph: List<Graph.Node>,
            val sink1: Graph.Sink<O1>,
            val sink2: Graph.Sink<O2>,
            val sink3: Graph.Sink<O3>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink3<I, O1, O2, O3>>() {
            override val sources: List<Graph.Source<*>> = listOf(source)
            override val sinks: List<Graph.Sink<*>> = listOf(sink1, sink2, sink3)

            suspend fun expect(input: I, dispatcher: CoroutineDispatcher = Dispatchers.Default): Triple<O1, O2, O3> = _expect(
                inputs = listOf(source.converter._encode(input)),
                dispatcher = dispatcher,
            ) { outputs ->
                Triple(
                    sink1.converter._decode(outputs[0]),
                    sink2.converter._decode(outputs[1]),
                    sink3.converter._decode(outputs[2]),
                )
            }

            suspend fun loss(input: I, label1: O1, label2: O2, label3: O3, dispatcher: CoroutineDispatcher = Dispatchers.Default): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                    { sink3.converter._encode(label3) },
                )
                val (loss1, loss2, loss3) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend inline fun loss(
                input: I,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                crossinline label3: (O3) -> O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                    {
                        val output = sink3.converter._decode(it)
                        sink3.converter._encode(label3(output))
                    },
                )
                val (loss1, loss2, loss3) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend fun train(input: I, label1: O1, label2: O2, label3: O3, dispatcher: CoroutineDispatcher = Dispatchers.Default): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                    { sink3.converter._encode(label3) },
                )
                val (loss1, loss2, loss3) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend inline fun train(
                input: I,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                crossinline label3: (O3) -> O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source.converter._encode(input))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                    {
                        val output = sink3.converter._decode(it)
                        sink3.converter._encode(label3(output))
                    },
                )
                val (loss1, loss2, loss3) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            fun <I2> replaceSource(converter: Converter<I2>): Sink3<I2, O1, O2, O3> {
                val copy = clone()
                return Sink3(
                    source = Graph.Source(id = copy.source.id, converter = converter),
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSink1D1")
            fun <T : Output.D1, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D1")
            fun <T : Output.D1, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D1")
            fun <T : Output.D1, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D2")
            fun <T : Output.D2, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D3")
            fun <T : Output.D3, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D2")
            fun <T : Output.D2, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D3")
            fun <T : Output.D3, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D2")
            fun <T : Output.D2, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D3")
            fun <T : Output.D3, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source = copy.source,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink3<I, O1, O2, O3> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink3<I, O1, O2, O3>> = serializer<I, O1, O2, O3>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I, O1, O2, O3> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink3<I, O1, O2, O3> {
                    check(sources.size == 1) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 3) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink3(
                        source = sources[0] as Graph.Source<I>,
                        graph = graph,
                        sink1 = sinks[0] as Graph.Sink<O1>,
                        sink2 = sinks[1] as Graph.Sink<O2>,
                        sink3 = sinks[2] as Graph.Sink<O3>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I, O1, O2, O3> serializer(): GraphNetworkSerializer<Sink3<I, O1, O2, O3>> = GraphNetworkSerializer(::build)

                fun <I, O1, O2, O3> fromJson(value: String): Sink3<I, O1, O2, O3> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I, O1, O2, O3> fromJson(source: BufferedSource): Sink3<I, O1, O2, O3> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I, O1, O2, O3> fromCbor(bytes: ByteArray): Sink3<I, O1, O2, O3> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I, O1, O2, O3> fromCbor(source: BufferedSource): Sink3<I, O1, O2, O3> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }
    }

    interface Src2 {
        class Sink1<I1, I2, O>(
            val source1: Graph.Source<I1>,
            val source2: Graph.Source<I2>,
            override val graph: List<Graph.Node>,
            val sink: Graph.Sink<O>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink1<I1, I2, O>>() {
            override val sources: List<Graph.Source<*>> = listOf(source1, source2)
            override val sinks: List<Graph.Sink<*>> = listOf(sink)

            suspend fun expect(input1: I1, input2: I2, dispatcher: CoroutineDispatcher = Dispatchers.Default): O = _expect(
                inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2)),
                dispatcher = dispatcher,
            ) { outputs -> sink.converter._decode(outputs[0]) }

            suspend fun loss(input1: I1, input2: I2, label: O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> { sink.converter._encode(label) }
                return _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend inline fun loss(input1: I1, input2: I2, crossinline label: (O) -> O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> {
                    val output = sink.converter._decode(it)
                    sink.converter._encode(label(output))
                }
                return _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend fun train(input1: I1, input2: I2, label: O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> { sink.converter._encode(label) }
                return _train(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend inline fun train(input1: I1, input2: I2, crossinline label: (O) -> O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> {
                    val output = sink.converter._decode(it)
                    sink.converter._encode(label(output))
                }
                return _train(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            fun <I1N> replaceSource1(converter: Converter<I1N>): Sink1<I1N, I2, O> {
                val copy = clone()
                return Sink1(
                    source1 = Graph.Source(id = copy.source1.id, converter = converter),
                    source2 = copy.source2,
                    graph = copy.graph,
                    sink = copy.sink,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I2N> replaceSource2(converter: Converter<I2N>): Sink1<I1, I2N, O> {
                val copy = clone()
                return Sink1(
                    source1 = copy.source1,
                    source2 = Graph.Source(id = copy.source2.id, converter = converter),
                    graph = copy.graph,
                    sink = copy.sink,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSinkD1")
            fun <T : Output.D1, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I1, I2, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSinkD2")
            fun <T : Output.D2, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I1, I2, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSinkD3")
            fun <T : Output.D3, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I1, I2, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink1<I1, I2, O> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink1<I1, I2, O>> = serializer<I1, I2, O>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I1, I2, O> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink1<I1, I2, O> {
                    check(sources.size == 2) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 1) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink1(
                        source1 = sources[0] as Graph.Source<I1>,
                        source2 = sources[1] as Graph.Source<I2>,
                        graph = graph,
                        sink = sinks[0] as Graph.Sink<O>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I1, I2, O> serializer(): GraphNetworkSerializer<Sink1<I1, I2, O>> = GraphNetworkSerializer(::build)

                fun <I1, I2, O> fromJson(value: String): Sink1<I1, I2, O> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I1, I2, O> fromJson(source: BufferedSource): Sink1<I1, I2, O> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I1, I2, O> fromCbor(bytes: ByteArray): Sink1<I1, I2, O> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I1, I2, O> fromCbor(source: BufferedSource): Sink1<I1, I2, O> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }

        class Sink2<I1, I2, O1, O2>(
            val source1: Graph.Source<I1>,
            val source2: Graph.Source<I2>,
            override val graph: List<Graph.Node>,
            val sink1: Graph.Sink<O1>,
            val sink2: Graph.Sink<O2>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink2<I1, I2, O1, O2>>() {
            override val sources: List<Graph.Source<*>> = listOf(source1, source2)
            override val sinks: List<Graph.Sink<*>> = listOf(sink1, sink2)

            suspend fun expect(input1: I1, input2: I2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<O1, O2> = _expect(
                inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2)),
                dispatcher = dispatcher,
            ) { outputs ->
                sink1.converter._decode(outputs[0]) to sink2.converter._decode(outputs[1])
            }

            suspend fun loss(input1: I1, input2: I2, label1: O1, label2: O2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                )
                val (loss1, loss2) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend inline fun loss(
                input1: I1,
                input2: I2,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                )
                val (loss1, loss2) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend fun train(input1: I1, input2: I2, label1: O1, label2: O2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                )
                val (loss1, loss2) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend inline fun train(
                input1: I1,
                input2: I2,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                )
                val (loss1, loss2) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            fun <I1N> replaceSource1(converter: Converter<I1N>): Sink2<I1N, I2, O1, O2> {
                val copy = clone()
                return Sink2(
                    source1 = Graph.Source(id = copy.source1.id, converter = converter),
                    source2 = copy.source2,
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I2N> replaceSource2(converter: Converter<I2N>): Sink2<I1, I2N, O1, O2> {
                val copy = clone()
                return Sink2(
                    source1 = copy.source1,
                    source2 = Graph.Source(id = copy.source2.id, converter = converter),
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSink1D1")
            fun <T : Output.D1, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D1")
            fun <T : Output.D1, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D2")
            fun <T : Output.D2, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D3")
            fun <T : Output.D3, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D2")
            fun <T : Output.D2, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D3")
            fun <T : Output.D3, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink2<I1, I2, O1, O2> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink2<I1, I2, O1, O2>> = serializer<I1, I2, O1, O2>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I1, I2, O1, O2> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink2<I1, I2, O1, O2> {
                    check(sources.size == 2) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 2) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink2(
                        source1 = sources[0] as Graph.Source<I1>,
                        source2 = sources[1] as Graph.Source<I2>,
                        graph = graph,
                        sink1 = sinks[0] as Graph.Sink<O1>,
                        sink2 = sinks[1] as Graph.Sink<O2>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I1, I2, O1, O2> serializer(): GraphNetworkSerializer<Sink2<I1, I2, O1, O2>> = GraphNetworkSerializer(::build)

                fun <I1, I2, O1, O2> fromJson(value: String): Sink2<I1, I2, O1, O2> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I1, I2, O1, O2> fromJson(source: BufferedSource): Sink2<I1, I2, O1, O2> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I1, I2, O1, O2> fromCbor(bytes: ByteArray): Sink2<I1, I2, O1, O2> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I1, I2, O1, O2> fromCbor(source: BufferedSource): Sink2<I1, I2, O1, O2> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }

        class Sink3<I1, I2, O1, O2, O3>(
            val source1: Graph.Source<I1>,
            val source2: Graph.Source<I2>,
            override val graph: List<Graph.Node>,
            val sink1: Graph.Sink<O1>,
            val sink2: Graph.Sink<O2>,
            val sink3: Graph.Sink<O3>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink3<I1, I2, O1, O2, O3>>() {
            override val sources: List<Graph.Source<*>> = listOf(source1, source2)
            override val sinks: List<Graph.Sink<*>> = listOf(sink1, sink2, sink3)

            suspend fun expect(input1: I1, input2: I2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Triple<O1, O2, O3> = _expect(
                inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2)),
                dispatcher = dispatcher,
            ) { outputs ->
                Triple(
                    sink1.converter._decode(outputs[0]),
                    sink2.converter._decode(outputs[1]),
                    sink3.converter._decode(outputs[2]),
                )
            }

            suspend fun loss(
                input1: I1,
                input2: I2,
                label1: O1,
                label2: O2,
                label3: O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                    { sink3.converter._encode(label3) },
                )
                val (loss1, loss2, loss3) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend inline fun loss(
                input1: I1,
                input2: I2,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                crossinline label3: (O3) -> O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                    {
                        val output = sink3.converter._decode(it)
                        sink3.converter._encode(label3(output))
                    },
                )
                val (loss1, loss2, loss3) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend fun train(
                input1: I1,
                input2: I2,
                label1: O1,
                label2: O2,
                label3: O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                    { sink3.converter._encode(label3) },
                )
                val (loss1, loss2, loss3) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend inline fun train(
                input1: I1,
                input2: I2,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                crossinline label3: (O3) -> O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(source1.converter._encode(input1), source2.converter._encode(input2))
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                    {
                        val output = sink3.converter._decode(it)
                        sink3.converter._encode(label3(output))
                    },
                )
                val (loss1, loss2, loss3) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            fun <I1N> replaceSource1(converter: Converter<I1N>): Sink3<I1N, I2, O1, O2, O3> {
                val copy = clone()
                return Sink3(
                    source1 = Graph.Source(id = copy.source1.id, converter = converter),
                    source2 = copy.source2,
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I2N> replaceSource2(converter: Converter<I2N>): Sink3<I1, I2N, O1, O2, O3> {
                val copy = clone()
                return Sink3(
                    source1 = copy.source1,
                    source2 = Graph.Source(id = copy.source2.id, converter = converter),
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSink1D1")
            fun <T : Output.D1, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D1")
            fun <T : Output.D1, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D1")
            fun <T : Output.D1, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D2")
            fun <T : Output.D2, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D3")
            fun <T : Output.D3, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D2")
            fun <T : Output.D2, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D3")
            fun <T : Output.D3, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D2")
            fun <T : Output.D2, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D3")
            fun <T : Output.D3, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink3<I1, I2, O1, O2, O3> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink3<I1, I2, O1, O2, O3>> = serializer<I1, I2, O1, O2, O3>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I1, I2, O1, O2, O3> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink3<I1, I2, O1, O2, O3> {
                    check(sources.size == 2) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 3) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink3(
                        source1 = sources[0] as Graph.Source<I1>,
                        source2 = sources[1] as Graph.Source<I2>,
                        graph = graph,
                        sink1 = sinks[0] as Graph.Sink<O1>,
                        sink2 = sinks[1] as Graph.Sink<O2>,
                        sink3 = sinks[2] as Graph.Sink<O3>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I1, I2, O1, O2, O3> serializer(): GraphNetworkSerializer<Sink3<I1, I2, O1, O2, O3>> = GraphNetworkSerializer(::build)

                fun <I1, I2, O1, O2, O3> fromJson(value: String): Sink3<I1, I2, O1, O2, O3> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I1, I2, O1, O2, O3> fromJson(source: BufferedSource): Sink3<I1, I2, O1, O2, O3> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I1, I2, O1, O2, O3> fromCbor(bytes: ByteArray): Sink3<I1, I2, O1, O2, O3> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I1, I2, O1, O2, O3> fromCbor(source: BufferedSource): Sink3<I1, I2, O1, O2, O3> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }
    }

    interface Src3 {
        class Sink1<I1, I2, I3, O>(
            val source1: Graph.Source<I1>,
            val source2: Graph.Source<I2>,
            val source3: Graph.Source<I3>,
            override val graph: List<Graph.Node>,
            val sink: Graph.Sink<O>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink1<I1, I2, I3, O>>() {
            override val sources: List<Graph.Source<*>> = listOf(source1, source2, source3)
            override val sinks: List<Graph.Sink<*>> = listOf(sink)

            suspend fun expect(input1: I1, input2: I2, input3: I3, dispatcher: CoroutineDispatcher = Dispatchers.Default): O = _expect(
                inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                ),
                dispatcher = dispatcher,
            ) { outputs -> sink.converter._decode(outputs[0]) }

            suspend fun loss(input1: I1, input2: I2, input3: I3, label: O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> { sink.converter._encode(label) }
                return _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend inline fun loss(input1: I1, input2: I2, input3: I3, crossinline label: (O) -> O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> {
                    val output = sink.converter._decode(it)
                    sink.converter._encode(label(output))
                }
                return _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend fun train(input1: I1, input2: I2, input3: I3, label: O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> { sink.converter._encode(label) }
                return _train(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            suspend inline fun train(input1: I1, input2: I2, input3: I3, crossinline label: (O) -> O, dispatcher: CoroutineDispatcher = Dispatchers.Default): IOType.D0.Global {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>> {
                    val output = sink.converter._decode(it)
                    sink.converter._encode(label(output))
                }
                return _train(inputs = inputs, labels = labels, dispatcher = dispatcher)[0]
            }

            fun <I1N> replaceSource1(converter: Converter<I1N>): Sink1<I1N, I2, I3, O> {
                val copy = clone()
                return Sink1(
                    source1 = Graph.Source(id = copy.source1.id, converter = converter),
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = copy.graph,
                    sink = copy.sink,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I2N> replaceSource2(converter: Converter<I2N>): Sink1<I1, I2N, I3, O> {
                val copy = clone()
                return Sink1(
                    source1 = copy.source1,
                    source2 = Graph.Source(id = copy.source2.id, converter = converter),
                    source3 = copy.source3,
                    graph = copy.graph,
                    sink = copy.sink,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I3N> replaceSource3(converter: Converter<I3N>): Sink1<I1, I2, I3N, O> {
                val copy = clone()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = Graph.Source(id = copy.source3.id, converter = converter),
                    graph = copy.graph,
                    sink = copy.sink,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSinkD1")
            fun <T : Output.D1, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I1, I2, I3, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSinkD2")
            fun <T : Output.D2, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I1, I2, I3, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSinkD3")
            fun <T : Output.D3, O2> replaceSink(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<O2>,
            ): Sink1<I1, I2, I3, O2> {
                val copy = clone()
                val last = copy.graph.last() as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink1(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink1<I1, I2, I3, O> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink1<I1, I2, I3, O>> = serializer<I1, I2, I3, O>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I1, I2, I3, O> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink1<I1, I2, I3, O> {
                    check(sources.size == 3) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 1) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink1(
                        source1 = sources[0] as Graph.Source<I1>,
                        source2 = sources[1] as Graph.Source<I2>,
                        source3 = sources[2] as Graph.Source<I3>,
                        graph = graph,
                        sink = sinks[0] as Graph.Sink<O>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I1, I2, I3, O> serializer(): GraphNetworkSerializer<Sink1<I1, I2, I3, O>> = GraphNetworkSerializer(::build)

                fun <I1, I2, I3, O> fromJson(value: String): Sink1<I1, I2, I3, O> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I1, I2, I3, O> fromJson(source: BufferedSource): Sink1<I1, I2, I3, O> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I1, I2, I3, O> fromCbor(bytes: ByteArray): Sink1<I1, I2, I3, O> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I1, I2, I3, O> fromCbor(source: BufferedSource): Sink1<I1, I2, I3, O> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }

        class Sink2<I1, I2, I3, O1, O2>(
            val source1: Graph.Source<I1>,
            val source2: Graph.Source<I2>,
            val source3: Graph.Source<I3>,
            override val graph: List<Graph.Node>,
            val sink1: Graph.Sink<O1>,
            val sink2: Graph.Sink<O2>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink2<I1, I2, I3, O1, O2>>() {
            override val sources: List<Graph.Source<*>> = listOf(source1, source2, source3)
            override val sinks: List<Graph.Sink<*>> = listOf(sink1, sink2)

            suspend fun expect(input1: I1, input2: I2, input3: I3, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<O1, O2> = _expect(
                inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                ),
                dispatcher = dispatcher,
            ) { outputs ->
                sink1.converter._decode(outputs[0]) to sink2.converter._decode(outputs[1])
            }

            suspend fun loss(input1: I1, input2: I2, input3: I3, label1: O1, label2: O2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                )
                val (loss1, loss2) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend inline fun loss(
                input1: I1,
                input2: I2,
                input3: I3,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                )
                val (loss1, loss2) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend fun train(input1: I1, input2: I2, input3: I3, label1: O1, label2: O2, dispatcher: CoroutineDispatcher = Dispatchers.Default): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                )
                val (loss1, loss2) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            suspend inline fun train(
                input1: I1,
                input2: I2,
                input3: I3,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Pair<IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                )
                val (loss1, loss2) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return loss1 to loss2
            }

            fun <I1N> replaceSource1(converter: Converter<I1N>): Sink2<I1N, I2, I3, O1, O2> {
                val copy = clone()
                return Sink2(
                    source1 = Graph.Source(id = copy.source1.id, converter = converter),
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I2N> replaceSource2(converter: Converter<I2N>): Sink2<I1, I2N, I3, O1, O2> {
                val copy = clone()
                return Sink2(
                    source1 = copy.source1,
                    source2 = Graph.Source(id = copy.source2.id, converter = converter),
                    source3 = copy.source3,
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I3N> replaceSource3(converter: Converter<I3N>): Sink2<I1, I2, I3N, O1, O2> {
                val copy = clone()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = Graph.Source(id = copy.source3.id, converter = converter),
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSink1D1")
            fun <T : Output.D1, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, I3, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D1")
            fun <T : Output.D1, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, I3, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D2")
            fun <T : Output.D2, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, I3, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D3")
            fun <T : Output.D3, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, I3, ON, O2> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D2")
            fun <T : Output.D2, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, I3, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D3")
            fun <T : Output.D3, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink2<I1, I2, I3, O1, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink2(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): Sink2<I1, I2, I3, O1, O2> =
                build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink2<I1, I2, I3, O1, O2>> = serializer<I1, I2, I3, O1, O2>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I1, I2, I3, O1, O2> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink2<I1, I2, I3, O1, O2> {
                    check(sources.size == 3) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 2) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink2(
                        source1 = sources[0] as Graph.Source<I1>,
                        source2 = sources[1] as Graph.Source<I2>,
                        source3 = sources[2] as Graph.Source<I3>,
                        graph = graph,
                        sink1 = sinks[0] as Graph.Sink<O1>,
                        sink2 = sinks[1] as Graph.Sink<O2>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I1, I2, I3, O1, O2> serializer(): GraphNetworkSerializer<Sink2<I1, I2, I3, O1, O2>> = GraphNetworkSerializer(::build)

                fun <I1, I2, I3, O1, O2> fromJson(value: String): Sink2<I1, I2, I3, O1, O2> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I1, I2, I3, O1, O2> fromJson(source: BufferedSource): Sink2<I1, I2, I3, O1, O2> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I1, I2, I3, O1, O2> fromCbor(bytes: ByteArray): Sink2<I1, I2, I3, O1, O2> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I1, I2, I3, O1, O2> fromCbor(source: BufferedSource): Sink2<I1, I2, I3, O1, O2> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }

        class Sink3<I1, I2, I3, O1, O2, O3>(
            val source1: Graph.Source<I1>,
            val source2: Graph.Source<I2>,
            val source3: Graph.Source<I3>,
            override val graph: List<Graph.Node>,
            val sink1: Graph.Sink<O1>,
            val sink2: Graph.Sink<O2>,
            val sink3: Graph.Sink<O3>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : GraphNetwork<Sink3<I1, I2, I3, O1, O2, O3>>() {
            override val sources: List<Graph.Source<*>> = listOf(source1, source2, source3)
            override val sinks: List<Graph.Sink<*>> = listOf(sink1, sink2, sink3)

            suspend fun expect(input1: I1, input2: I2, input3: I3, dispatcher: CoroutineDispatcher = Dispatchers.Default): Triple<O1, O2, O3> = _expect(
                inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                ),
                dispatcher = dispatcher,
            ) { outputs ->
                Triple(
                    sink1.converter._decode(outputs[0]),
                    sink2.converter._decode(outputs[1]),
                    sink3.converter._decode(outputs[2]),
                )
            }

            suspend fun loss(
                input1: I1,
                input2: I2,
                input3: I3,
                label1: O1,
                label2: O2,
                label3: O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                    { sink3.converter._encode(label3) },
                )
                val (loss1, loss2, loss3) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend inline fun loss(
                input1: I1,
                input2: I2,
                input3: I3,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                crossinline label3: (O3) -> O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                    {
                        val output = sink3.converter._decode(it)
                        sink3.converter._encode(label3(output))
                    },
                )
                val (loss1, loss2, loss3) = _loss(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend fun train(
                input1: I1,
                input2: I2,
                input3: I3,
                label1: O1,
                label2: O2,
                label3: O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    { sink1.converter._encode(label1) },
                    { sink2.converter._encode(label2) },
                    { sink3.converter._encode(label3) },
                )
                val (loss1, loss2, loss3) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            suspend inline fun train(
                input1: I1,
                input2: I2,
                input3: I3,
                crossinline label1: (O1) -> O1,
                crossinline label2: (O2) -> O2,
                crossinline label3: (O3) -> O3,
                dispatcher: CoroutineDispatcher = Dispatchers.Default,
            ): Triple<IOType.D0.Global, IOType.D0.Global, IOType.D0.Global> {
                val inputs = listOf(
                    source1.converter._encode(input1),
                    source2.converter._encode(input2),
                    source3.converter._encode(input3),
                )
                val labels = listOf<(Batch<IOType>) -> Batch<IOType>>(
                    {
                        val output = sink1.converter._decode(it)
                        sink1.converter._encode(label1(output))
                    },
                    {
                        val output = sink2.converter._decode(it)
                        sink2.converter._encode(label2(output))
                    },
                    {
                        val output = sink3.converter._decode(it)
                        sink3.converter._encode(label3(output))
                    },
                )
                val (loss1, loss2, loss3) = _train(inputs = inputs, labels = labels, dispatcher = dispatcher)
                return Triple(loss1, loss2, loss3)
            }

            fun <I1N> replaceSource1(converter: Converter<I1N>): Sink3<I1N, I2, I3, O1, O2, O3> {
                val copy = clone()
                return Sink3(
                    source1 = Graph.Source(id = copy.source1.id, converter = converter),
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I2N> replaceSource2(converter: Converter<I2N>): Sink3<I1, I2N, I3, O1, O2, O3> {
                val copy = clone()
                return Sink3(
                    source1 = copy.source1,
                    source2 = Graph.Source(id = copy.source2.id, converter = converter),
                    source3 = copy.source3,
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            fun <I3N> replaceSource3(converter: Converter<I3N>): Sink3<I1, I2, I3N, O1, O2, O3> {
                val copy = clone()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = Graph.Source(id = copy.source3.id, converter = converter),
                    graph = copy.graph,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = copy.optimizer,
                    initializer = copy.initializer,
                )
            }

            @JvmName("replaceSink1D1")
            fun <T : Output.D1, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D1")
            fun <T : Output.D1, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D1")
            fun <T : Output.D1, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D1.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 1) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D1(
                    inputI = last.process.outputShape[0],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D2")
            fun <T : Output.D2, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink1D3")
            fun <T : Output.D3, ON> replaceSink1(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, ON, O2, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink1.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink1. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink1.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = result.sink,
                    sink2 = copy.sink2,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D2")
            fun <T : Output.D2, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink2D3")
            fun <T : Output.D3, ON> replaceSink2(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, O1, ON, O3> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink2.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink2. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink2.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = result.sink,
                    sink3 = copy.sink3,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D2")
            fun <T : Output.D2, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D2.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 2) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D2(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            @JvmName("replaceSink3D3")
            fun <T : Output.D3, ON> replaceSink3(
                optimizer: Optimizer = this.optimizer,
                initializer: WeightInitializer = this.initializer,
                block: GraphBuilder.Node.D3.() -> GraphBuilder.Result.Sink1<ON>,
            ): Sink3<I1, I2, I3, O1, O2, ON> {
                val copy = clone()
                val last = copy.graph.first { it.id == copy.sink3.from } as Graph.Node.Attach
                check(last.process.outputShape.size == 3) {
                    "invalid replaceSink3. outputShape=${last.process.outputShape}"
                }
                val builder = GraphBuilder.Node.D3(
                    inputI = last.process.outputShape[0],
                    inputJ = last.process.outputShape[1],
                    inputK = last.process.outputShape[2],
                    from = copy.sink3.from,
                    nodes = copy.graph,
                    optimizer = optimizer,
                    initializer = initializer,
                )
                val result = builder.block()
                return Sink3(
                    source1 = copy.source1,
                    source2 = copy.source2,
                    source3 = copy.source3,
                    graph = result.nodes,
                    sink1 = copy.sink1,
                    sink2 = copy.sink2,
                    sink3 = result.sink,
                    optimizer = optimizer,
                    initializer = initializer,
                )
            }

            override fun create(
                sources: List<Graph.Source<*>>,
                graph: List<Graph.Node>,
                sinks: List<Graph.Sink<*>>,
                optimizer: Optimizer,
                initializer: WeightInitializer,
            ): Sink3<I1, I2, I3, O1, O2, O3> = build(sources, graph, sinks, optimizer, initializer)

            override fun serializer(): GraphNetworkSerializer<Sink3<I1, I2, I3, O1, O2, O3>> = serializer<I1, I2, I3, O1, O2, O3>()

            companion object {
                @Suppress("UNCHECKED_CAST")
                private fun <I1, I2, I3, O1, O2, O3> build(
                    sources: List<Graph.Source<*>>,
                    graph: List<Graph.Node>,
                    sinks: List<Graph.Sink<*>>,
                    optimizer: Optimizer,
                    initializer: WeightInitializer,
                ): Sink3<I1, I2, I3, O1, O2, O3> {
                    check(sources.size == 3) { "invalid Network format. sources.size=${sources.size}." }
                    check(sinks.size == 3) { "invalid Network format. sinks.size=${sinks.size}." }
                    return Sink3(
                        source1 = sources[0] as Graph.Source<I1>,
                        source2 = sources[1] as Graph.Source<I2>,
                        source3 = sources[2] as Graph.Source<I3>,
                        graph = graph,
                        sink1 = sinks[0] as Graph.Sink<O1>,
                        sink2 = sinks[1] as Graph.Sink<O2>,
                        sink3 = sinks[2] as Graph.Sink<O3>,
                        optimizer = optimizer,
                        initializer = initializer,
                    )
                }

                private fun <I1, I2, I3, O1, O2, O3> serializer(): GraphNetworkSerializer<Sink3<I1, I2, I3, O1, O2, O3>> = GraphNetworkSerializer(::build)

                fun <I1, I2, I3, O1, O2, O3> fromJson(value: String): Sink3<I1, I2, I3, O1, O2, O3> = networkSerializerJson.decodeFromString(serializer(), value)

                fun <I1, I2, I3, O1, O2, O3> fromJson(source: BufferedSource): Sink3<I1, I2, I3, O1, O2, O3> = networkSerializerJson.decodeFromBufferedSource(serializer(), source)

                fun <I1, I2, I3, O1, O2, O3> fromCbor(bytes: ByteArray): Sink3<I1, I2, I3, O1, O2, O3> = networkSerializerCbor.decodeFromByteArray(serializer(), bytes)

                fun <I1, I2, I3, O1, O2, O3> fromCbor(source: BufferedSource): Sink3<I1, I2, I3, O1, O2, O3> = networkSerializerCbor.decodeFromByteArray(serializer(), source.readByteArray())
            }
        }
    }

    companion object
}
