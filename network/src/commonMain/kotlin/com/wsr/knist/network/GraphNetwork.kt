@file:OptIn(ExperimentalSerializationApi::class)

package com.wsr.knist.network

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.launch
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import com.wsr.knist.network.process.Process
import com.wsr.knist.network.process.Reshape
import kotlin.jvm.JvmName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.BufferedSink

private typealias TrainLambda = IOScope.(GraphEnv) -> Unit

abstract class GraphNetwork<T : GraphNetwork<T>> {
    abstract val sources: List<Graph.Source<*>>
    abstract val graph: List<Graph.Node>
    abstract val sinks: List<Graph.Sink<*>>

    abstract val optimizer: Optimizer
    abstract val initializer: WeightInitializer

    @PublishedApi
    internal val mutex = Mutex()

    @Suppress("FunctionName")
    protected suspend inline fun <O> _expect(inputs: List<Batch<IOType>>, dispatcher: CoroutineDispatcher = Dispatchers.Default, crossinline decode: (outputs: List<Batch<IOType>>) -> O): O =
        withContext(dispatcher) {
            val env = GraphEnv()
            sources.forEachIndexed { i, source -> env[source.id] = inputs[i] }
            IOScope.launch {
                val scope = this
                graph.forEach { node ->
                    when (node) {
                        is Graph.Node.Attach -> {
                            with(node.process) {
                                env[node.id] = scope._expect(env[node.from], env)
                            }
                        }

                        is Graph.Node.Connect -> {
                            with(node.join) {
                                env[node.id] = scope._expect(node.from.map { env[it] }, env)
                            }
                        }

                        is Graph.Node.Observe -> {
                            env[node.id] = env[node.from]
                        }
                    }
                }
                val outputs = sinks.map { sink -> with(sink.output) { scope._expect(env[sink.from]) } }
                decode(outputs)
            }
        }

    @Suppress("UNCHECKED_CAST", "FunctionName")
    @PublishedApi
    internal suspend fun _loss(inputs: List<Batch<IOType>>, labels: List<(output: Batch<IOType>) -> Batch<IOType>>, dispatcher: CoroutineDispatcher = Dispatchers.Default): List<IOType.D0.Global> =
        withContext(dispatcher) {
            val env = GraphEnv()
            sources.forEachIndexed { i, source -> env[source.id] = inputs[i] }
            IOScope.launch {
                val scope = this
                graph.forEach { node ->
                    when (node) {
                        is Graph.Node.Attach -> {
                            with(node.process) {
                                env[node.id] = scope._expect(env[node.from], env)
                            }
                        }

                        is Graph.Node.Connect -> {
                            with(node.join) {
                                env[node.id] = scope._expect(node.from.map { env[it] }, env)
                            }
                        }

                        is Graph.Node.Observe -> {
                            env[node.id] = env[node.from]
                        }
                    }
                }
                sinks.mapIndexed { i, sink ->
                    val result = with(sink.output) {
                        scope._train(input = env[sink.from], label = labels[i])
                    }
                    result.loss.toGlobal()
                }
            }
        }

    private val trainLambda: (TrainLambda) -> TrainLambda by lazy {
        val initial: (TrainLambda) -> TrainLambda = { it }
        graph.foldRight(initial) { node, next ->
            { final ->
                when (node) {
                    is Graph.Node.Attach -> {
                        { env ->
                            val delta = with(node.process) {
                                _train(env[node.from], env) { out ->
                                    env[node.id] = out
                                    next(final)(env)
                                    env[node.id]
                                }
                            }
                            env.plus(node.from, delta)
                        }
                    }

                    is Graph.Node.Connect -> {
                        { env ->
                            val delta = with(node.join) {
                                _train(node.from.map { env[it] }, env) { out ->
                                    env[node.id] = out
                                    next(final)(env)
                                    env[node.id]
                                }
                            }
                            node.from.forEachIndexed { index, id -> env.plus(id, delta[index]) }
                        }
                    }

                    is Graph.Node.Observe -> {
                        { env ->
                            env[node.id] = env[node.from]
                            next(final)(env)
                        }
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST", "FunctionName")
    @PublishedApi
    internal suspend fun _train(inputs: List<Batch<IOType>>, labels: List<(output: Batch<IOType>) -> Batch<IOType>>, dispatcher: CoroutineDispatcher = Dispatchers.Default): List<IOType.D0.Global> =
        withContext(dispatcher) {
            mutex.withLock {
                val env = GraphEnv()
                sources.forEachIndexed { i, source -> env[source.id] = inputs[i] }
                IOScope.launch {
                    var losses: List<IOType.D0.Global>? = null
                    val sinkStep: TrainLambda = {
                        val outputs = sinks.map { sink -> env.get<IOType>(sink.from) }
                        env.reset()
                        losses = sinks.mapIndexed { i, sink ->
                            val result = with(sink.output) { _train(outputs[i], labels[i]) }
                            env.plus(sink.from, result.delta)
                            result.loss.toGlobal()
                        }
                    }
                    trainLambda(sinkStep)(env)
                    losses!!
                }
            }
        }

    @JvmName("replaceOptimizer")
    fun replace(condition: (Process) -> Boolean, optimizer: Optimizer): T = clone().also { copy ->
        copy.graph.forEach { node ->
            if (node is Graph.Node.Attach && condition(node.process)) {
                node.process.update(optimizer)
            }
        }
    }

    @JvmName("replaceComputeD1")
    inline fun <reified C : Compute.D1> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (C) -> Boolean,
        crossinline block: GraphBuilder.Node.D1.(C) -> GraphBuilder.Node.D1,
    ): T = replace<C, GraphBuilder.Node.D1, GraphBuilder.Node.D1>(
        seed = { layer, from ->
            GraphBuilder.Node.D1(
                inputI = layer.inputI,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceComputeD2")
    inline fun <reified C : Compute.D2> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (C) -> Boolean,
        crossinline block: GraphBuilder.Node.D2.(C) -> GraphBuilder.Node.D2,
    ): T = replace<C, GraphBuilder.Node.D2, GraphBuilder.Node.D2>(
        seed = { layer, from ->
            GraphBuilder.Node.D2(
                inputI = layer.inputI,
                inputJ = layer.inputJ,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceComputeD3")
    inline fun <reified C : Compute.D3> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (C) -> Boolean,
        crossinline block: GraphBuilder.Node.D3.(C) -> GraphBuilder.Node.D3,
    ): T = replace<C, GraphBuilder.Node.D3, GraphBuilder.Node.D3>(
        seed = { layer, from ->
            GraphBuilder.Node.D3(
                inputI = layer.inputI,
                inputJ = layer.inputJ,
                inputK = layer.inputK,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceReshapeD1ToD2")
    inline fun <reified R : Reshape.D1ToD2> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (R) -> Boolean,
        crossinline block: GraphBuilder.Node.D1.(R) -> GraphBuilder.Node.D2,
    ): T = replace<R, GraphBuilder.Node.D1, GraphBuilder.Node.D2>(
        seed = { layer, from ->
            GraphBuilder.Node.D1(
                inputI = layer.inputI,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceReshapeD1ToD3")
    inline fun <reified R : Reshape.D1ToD3> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (R) -> Boolean,
        crossinline block: GraphBuilder.Node.D1.(R) -> GraphBuilder.Node.D3,
    ): T = replace<R, GraphBuilder.Node.D1, GraphBuilder.Node.D3>(
        seed = { layer, from ->
            GraphBuilder.Node.D1(
                inputI = layer.inputI,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceReshapeD2ToD1")
    inline fun <reified R : Reshape.D2ToD1> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (R) -> Boolean,
        crossinline block: GraphBuilder.Node.D2.(R) -> GraphBuilder.Node.D1,
    ): T = replace<R, GraphBuilder.Node.D2, GraphBuilder.Node.D1>(
        seed = { layer, from ->
            GraphBuilder.Node.D2(
                inputI = layer.inputI,
                inputJ = layer.inputJ,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceReshapeD2ToD3")
    inline fun <reified R : Reshape.D2ToD3> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (R) -> Boolean,
        crossinline block: GraphBuilder.Node.D2.(R) -> GraphBuilder.Node.D3,
    ): T = replace<R, GraphBuilder.Node.D2, GraphBuilder.Node.D3>(
        seed = { layer, from ->
            GraphBuilder.Node.D2(
                inputI = layer.inputI,
                inputJ = layer.inputJ,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceReshapeD3ToD1")
    inline fun <reified R : Reshape.D3ToD1> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (R) -> Boolean,
        crossinline block: GraphBuilder.Node.D3.(R) -> GraphBuilder.Node.D1,
    ): T = replace<R, GraphBuilder.Node.D3, GraphBuilder.Node.D1>(
        seed = { layer, from ->
            GraphBuilder.Node.D3(
                inputI = layer.inputI,
                inputJ = layer.inputJ,
                inputK = layer.inputK,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @JvmName("replaceReshapeD3ToD2")
    inline fun <reified R : Reshape.D3ToD2> replace(
        optimizer: Optimizer = this.optimizer,
        initializer: WeightInitializer = this.initializer,
        crossinline condition: (R) -> Boolean,
        crossinline block: GraphBuilder.Node.D3.(R) -> GraphBuilder.Node.D2,
    ): T = replace<R, GraphBuilder.Node.D3, GraphBuilder.Node.D2>(
        seed = { layer, from ->
            GraphBuilder.Node.D3(
                inputI = layer.inputI,
                inputJ = layer.inputJ,
                inputK = layer.inputK,
                from = from,
                nodes = emptyList(),
                optimizer = optimizer,
                initializer = initializer,
            )
        },
        condition = condition,
        block = block,
    )

    @PublishedApi
    internal inline fun <reified P : Process, B1 : GraphBuilder.Node, B2 : GraphBuilder.Node> replace(
        crossinline seed: (layer: P, from: GraphId) -> B1,
        crossinline condition: (P) -> Boolean,
        crossinline block: B1.(P) -> B2,
    ): T {
        val copy = clone()
        val redirect = mutableMapOf<GraphId, GraphId>()
        val newNodes = copy.graph.fold(emptyList<Graph.Node>()) { nodes, node ->
            when (node) {
                is Graph.Node.Attach -> {
                    val process = node.process
                    if (process !is P || !condition(process)) {
                        return@fold nodes + Graph.Node.Attach(
                            id = node.id,
                            from = redirect[node.from] ?: node.from,
                            process = process,
                        )
                    }

                    val from = redirect[node.from] ?: node.from
                    val result = seed(process, from).block(process)
                    if (result.nodes.isEmpty()) {
                        check(process.inputShape == process.outputShape) {
                            """
                                invalid replace.
                                input: ${process.inputShape}
                                output: ${process.outputShape}
                            """.trimIndent()
                        }
                        redirect[node.id] = from
                        return@fold nodes
                    }

                    val first = (result.nodes.first() as Graph.Node.Attach).process
                    val last = (result.nodes.last() as Graph.Node.Attach).process
                    check(first.inputShape == process.inputShape && last.outputShape == process.outputShape) {
                        """
                            invalid replace.
                            input: ${process.inputShape}
                            output: ${process.outputShape}
                            replaced input: ${first.inputShape}
                            replaced output: ${last.outputShape}
                        """.trimIndent()
                    }
                    redirect[node.id] = result.from
                    nodes + result.nodes
                }

                is Graph.Node.Connect -> nodes + Graph.Node.Connect(
                    id = node.id,
                    from = node.from.map { redirect[it] ?: it },
                    join = node.join,
                )

                is Graph.Node.Observe -> nodes + Graph.Node.Observe(
                    id = node.id,
                    from = redirect[node.from] ?: node.from,
                )
            }
        }
        return create(
            sources = copy.sources,
            graph = newNodes,
            sinks = copy.sinks.map { sink ->
                Graph.Sink(
                    id = sink.id,
                    from = redirect[sink.from] ?: sink.from,
                    output = sink.output,
                    converter = sink.converter,
                )
            },
            optimizer = copy.optimizer,
            initializer = copy.initializer,
        )
    }

    @PublishedApi
    internal abstract fun create(sources: List<Graph.Source<*>>, graph: List<Graph.Node>, sinks: List<Graph.Sink<*>>, optimizer: Optimizer, initializer: WeightInitializer): T

    internal abstract fun serializer(): GraphNetworkSerializer<T>

    @Suppress("UNCHECKED_CAST")
    fun toJson(): String = networkSerializerJson.encodeToString(serializer(), this as T)

    @Suppress("UNCHECKED_CAST")
    fun toJson(sink: BufferedSink) {
        networkSerializerJson.encodeToBufferedSink(serializer(), this as T, sink)
    }

    @Suppress("UNCHECKED_CAST")
    fun toCbor(): ByteArray = networkSerializerCbor.encodeToByteArray(serializer(), this as T)

    @Suppress("UNCHECKED_CAST")
    fun toCbor(sink: BufferedSink) {
        sink.write(networkSerializerCbor.encodeToByteArray(serializer(), this as T))
    }

    @Suppress("UNCHECKED_CAST")
    fun clone(): T = networkSerializerCbor.decodeFromByteArray(
        serializer(),
        networkSerializerCbor.encodeToByteArray(serializer(), this as T),
    )
}
