package com.wsr.knist.network

import com.wsr.knist.network.converter.Converter
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.join.Join
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.process.Compute
import com.wsr.knist.network.process.Reshape
import kotlin.experimental.ExperimentalTypeInference

object GraphBuilder {
    sealed interface Node {
        val from: GraphId
        val nodes: List<Graph.Node>
        val optimizer: Optimizer
        val initializer: WeightInitializer

        data class D1(val inputI: Int, override val from: GraphId, override val nodes: List<Graph.Node>, override val optimizer: Optimizer, override val initializer: WeightInitializer) : Node

        data class D2(
            val inputI: Int,
            val inputJ: Int,
            override val from: GraphId,
            override val nodes: List<Graph.Node>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : Node

        data class D3(
            val inputI: Int,
            val inputJ: Int,
            val inputK: Int,
            override val from: GraphId,
            override val nodes: List<Graph.Node>,
            override val optimizer: Optimizer,
            override val initializer: WeightInitializer,
        ) : Node
    }

    sealed interface Result {
        data class Sink1<O>(val nodes: List<Graph.Node>, val sink: Graph.Sink<O>)
        data class Sink2<O1, O2>(val nodes: List<Graph.Node>, val sink1: Graph.Sink<O1>, val sink2: Graph.Sink<O2>)
        data class Sink3<O1, O2, O3>(val nodes: List<Graph.Node>, val sink1: Graph.Sink<O1>, val sink2: Graph.Sink<O2>, val sink3: Graph.Sink<O3>)
    }
}

object GraphScope {
    fun GraphBuilder.Node.D1.repeat(times: Int, builder: GraphBuilder.Node.D1.(index: Int) -> GraphBuilder.Node.D1): GraphBuilder.Node.D1 = (0 until times).fold(this) { acc, i -> acc.builder(i) }

    fun GraphBuilder.Node.D2.repeat(times: Int, builder: GraphBuilder.Node.D2.(index: Int) -> GraphBuilder.Node.D2): GraphBuilder.Node.D2 = (0 until times).fold(this) { acc, i -> acc.builder(i) }

    fun GraphBuilder.Node.D3.repeat(times: Int, builder: GraphBuilder.Node.D3.(index: Int) -> GraphBuilder.Node.D3): GraphBuilder.Node.D3 = (0 until times).fold(this) { acc, i -> acc.builder(i) }

    fun GraphBuilder.Node.D1.addCompute(compute: Compute.D1): GraphBuilder.Node.D1 {
        val node = Graph.Node.Attach(from = from, process = compute)
        return GraphBuilder.Node.D1(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = compute.outputI,
        )
    }

    fun GraphBuilder.Node.D2.addCompute(compute: Compute.D2): GraphBuilder.Node.D2 {
        val node = Graph.Node.Attach(from = from, process = compute)
        return GraphBuilder.Node.D2(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = compute.outputI,
            inputJ = compute.outputJ,
        )
    }

    fun GraphBuilder.Node.D3.addCompute(compute: Compute.D3): GraphBuilder.Node.D3 {
        val node = Graph.Node.Attach(from = from, process = compute)
        return GraphBuilder.Node.D3(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = compute.outputI,
            inputJ = compute.outputJ,
            inputK = compute.outputK,
        )
    }

    fun GraphBuilder.Node.D1.addReshape(reshape: Reshape.D1ToD2): GraphBuilder.Node.D2 {
        val node = Graph.Node.Attach(from = from, process = reshape)
        return GraphBuilder.Node.D2(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = reshape.outputI,
            inputJ = reshape.outputJ,
        )
    }

    fun GraphBuilder.Node.D1.addReshape(reshape: Reshape.D1ToD3): GraphBuilder.Node.D3 {
        val node = Graph.Node.Attach(from = from, process = reshape)
        return GraphBuilder.Node.D3(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = reshape.outputI,
            inputJ = reshape.outputJ,
            inputK = reshape.outputK,
        )
    }

    fun GraphBuilder.Node.D2.addReshape(reshape: Reshape.D2ToD1): GraphBuilder.Node.D1 {
        val node = Graph.Node.Attach(from = from, process = reshape)
        return GraphBuilder.Node.D1(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = reshape.outputI,
        )
    }

    fun GraphBuilder.Node.D2.addReshape(reshape: Reshape.D2ToD3): GraphBuilder.Node.D3 {
        val node = Graph.Node.Attach(from = from, process = reshape)
        return GraphBuilder.Node.D3(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = reshape.outputI,
            inputJ = reshape.outputJ,
            inputK = reshape.outputK,
        )
    }

    fun GraphBuilder.Node.D3.addReshape(reshape: Reshape.D3ToD1): GraphBuilder.Node.D1 {
        val node = Graph.Node.Attach(from = from, process = reshape)
        return GraphBuilder.Node.D1(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = reshape.outputI,
        )
    }

    fun GraphBuilder.Node.D3.addReshape(reshape: Reshape.D3ToD2): GraphBuilder.Node.D2 {
        val node = Graph.Node.Attach(from = from, process = reshape)
        return GraphBuilder.Node.D2(
            from = node.id,
            nodes = nodes + node,
            optimizer = optimizer,
            initializer = initializer,
            inputI = reshape.outputI,
            inputJ = reshape.outputJ,
        )
    }

    fun addJoin(join: Join.D1, vararg nodes: GraphBuilder.Node.D1): GraphBuilder.Node.D1 {
        val node = Graph.Node.Connect(from = nodes.map { it.from }, join = join)
        return GraphBuilder.Node.D1(
            from = node.id,
            nodes = (nodes.flatMap { it.nodes } + node).distinctBy { it.id },
            optimizer = nodes.first().optimizer,
            initializer = nodes.first().initializer,
            inputI = join.outputI,
        )
    }

    fun addJoin(join: Join.D2, vararg nodes: GraphBuilder.Node.D2): GraphBuilder.Node.D2 {
        val node = Graph.Node.Connect(from = nodes.map { it.from }, join = join)
        return GraphBuilder.Node.D2(
            from = node.id,
            nodes = (nodes.flatMap { it.nodes } + node).distinctBy { it.id },
            optimizer = nodes.first().optimizer,
            initializer = nodes.first().initializer,
            inputI = join.outputI,
            inputJ = join.outputJ,
        )
    }

    fun addJoin(join: Join.D3, vararg nodes: GraphBuilder.Node.D3): GraphBuilder.Node.D3 {
        val node = Graph.Node.Connect(from = nodes.map { it.from }, join = join)
        return GraphBuilder.Node.D3(
            from = node.id,
            nodes = (nodes.flatMap { it.nodes } + node).distinctBy { it.id },
            optimizer = nodes.first().optimizer,
            initializer = nodes.first().initializer,
            inputI = join.outputI,
            inputJ = join.outputJ,
            inputK = join.outputK,
        )
    }

    fun <O> GraphBuilder.Node.D1.addOutput(output: Output.D1, converter: Converter<O>): GraphBuilder.Result.Sink1<O> {
        val sink = Graph.Sink(from = from, output = output, converter = converter)
        return GraphBuilder.Result.Sink1(nodes = nodes, sink = sink)
    }

    fun <O> GraphBuilder.Node.D2.addOutput(output: Output.D2, converter: Converter<O>): GraphBuilder.Result.Sink1<O> {
        val sink = Graph.Sink(from = from, output = output, converter = converter)
        return GraphBuilder.Result.Sink1(nodes = nodes, sink = sink)
    }

    fun <O1, O2> outputs(o1: GraphBuilder.Result.Sink1<O1>, o2: GraphBuilder.Result.Sink1<O2>): GraphBuilder.Result.Sink2<O1, O2> = GraphBuilder.Result.Sink2(
        nodes = (o1.nodes + o2.nodes).distinctBy { it.id },
        sink1 = o1.sink,
        sink2 = o2.sink,
    )

    fun <O1, O2, O3> outputs(o1: GraphBuilder.Result.Sink1<O1>, o2: GraphBuilder.Result.Sink1<O2>, o3: GraphBuilder.Result.Sink1<O3>): GraphBuilder.Result.Sink3<O1, O2, O3> =
        GraphBuilder.Result.Sink3(
            nodes = (o1.nodes + o2.nodes + o3.nodes).distinctBy { it.id },
            sink1 = o1.sink,
            sink2 = o2.sink,
            sink3 = o3.sink,
        )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I, D : GraphBuilder.Node, O> Network.Companion.create(
    port: Port<I, D>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D) -> GraphBuilder.Result.Sink1<O>,
): Network.Src1.Sink1<I, O> {
    val (source, seed) = port.toNode(optimizer, initializer)
    val result = GraphScope.block(seed)
    return Network.Src1.Sink1(
        source = source,
        graph = result.nodes,
        sink = result.sink,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I, D : GraphBuilder.Node, O1, O2> Network.Companion.create(
    port: Port<I, D>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D) -> GraphBuilder.Result.Sink2<O1, O2>,
): Network.Src1.Sink2<I, O1, O2> {
    val (source, seed) = port.toNode(optimizer, initializer)
    val result = GraphScope.block(seed)
    return Network.Src1.Sink2(
        source = source,
        graph = result.nodes,
        sink1 = result.sink1,
        sink2 = result.sink2,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I1, I2, D1 : GraphBuilder.Node, D2 : GraphBuilder.Node, O> Network.Companion.create(
    port1: Port<I1, D1>,
    port2: Port<I2, D2>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D1, D2) -> GraphBuilder.Result.Sink1<O>,
): Network.Src2.Sink1<I1, I2, O> {
    val (source1, seed1) = port1.toNode(optimizer, initializer)
    val (source2, seed2) = port2.toNode(optimizer, initializer)
    val result = GraphScope.block(seed1, seed2)
    return Network.Src2.Sink1(
        source1 = source1,
        source2 = source2,
        graph = result.nodes,
        sink = result.sink,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I1, I2, D1 : GraphBuilder.Node, D2 : GraphBuilder.Node, O1, O2> Network.Companion.create(
    port1: Port<I1, D1>,
    port2: Port<I2, D2>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D1, D2) -> GraphBuilder.Result.Sink2<O1, O2>,
): Network.Src2.Sink2<I1, I2, O1, O2> {
    val (source1, seed1) = port1.toNode(optimizer, initializer)
    val (source2, seed2) = port2.toNode(optimizer, initializer)
    val result = GraphScope.block(seed1, seed2)
    return Network.Src2.Sink2(
        source1 = source1,
        source2 = source2,
        graph = result.nodes,
        sink1 = result.sink1,
        sink2 = result.sink2,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I, D : GraphBuilder.Node, O1, O2, O3> Network.Companion.create(
    port: Port<I, D>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D) -> GraphBuilder.Result.Sink3<O1, O2, O3>,
): Network.Src1.Sink3<I, O1, O2, O3> {
    val (source, seed) = port.toNode(optimizer, initializer)
    val result = GraphScope.block(seed)
    return Network.Src1.Sink3(
        source = source,
        graph = result.nodes,
        sink1 = result.sink1,
        sink2 = result.sink2,
        sink3 = result.sink3,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I1, I2, D1 : GraphBuilder.Node, D2 : GraphBuilder.Node, O1, O2, O3> Network.Companion.create(
    port1: Port<I1, D1>,
    port2: Port<I2, D2>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D1, D2) -> GraphBuilder.Result.Sink3<O1, O2, O3>,
): Network.Src2.Sink3<I1, I2, O1, O2, O3> {
    val (source1, seed1) = port1.toNode(optimizer, initializer)
    val (source2, seed2) = port2.toNode(optimizer, initializer)
    val result = GraphScope.block(seed1, seed2)
    return Network.Src2.Sink3(
        source1 = source1,
        source2 = source2,
        graph = result.nodes,
        sink1 = result.sink1,
        sink2 = result.sink2,
        sink3 = result.sink3,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I1, I2, I3, D1 : GraphBuilder.Node, D2 : GraphBuilder.Node, D3 : GraphBuilder.Node, O> Network.Companion.create(
    port1: Port<I1, D1>,
    port2: Port<I2, D2>,
    port3: Port<I3, D3>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D1, D2, D3) -> GraphBuilder.Result.Sink1<O>,
): Network.Src3.Sink1<I1, I2, I3, O> {
    val (source1, seed1) = port1.toNode(optimizer, initializer)
    val (source2, seed2) = port2.toNode(optimizer, initializer)
    val (source3, seed3) = port3.toNode(optimizer, initializer)
    val result = GraphScope.block(seed1, seed2, seed3)
    return Network.Src3.Sink1(
        source1 = source1,
        source2 = source2,
        source3 = source3,
        graph = result.nodes,
        sink = result.sink,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I1, I2, I3, D1 : GraphBuilder.Node, D2 : GraphBuilder.Node, D3 : GraphBuilder.Node, O1, O2> Network.Companion.create(
    port1: Port<I1, D1>,
    port2: Port<I2, D2>,
    port3: Port<I3, D3>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D1, D2, D3) -> GraphBuilder.Result.Sink2<O1, O2>,
): Network.Src3.Sink2<I1, I2, I3, O1, O2> {
    val (source1, seed1) = port1.toNode(optimizer, initializer)
    val (source2, seed2) = port2.toNode(optimizer, initializer)
    val (source3, seed3) = port3.toNode(optimizer, initializer)
    val result = GraphScope.block(seed1, seed2, seed3)
    return Network.Src3.Sink2(
        source1 = source1,
        source2 = source2,
        source3 = source3,
        graph = result.nodes,
        sink1 = result.sink1,
        sink2 = result.sink2,
        optimizer = optimizer,
        initializer = initializer,
    )
}

@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
fun <I1, I2, I3, D1 : GraphBuilder.Node, D2 : GraphBuilder.Node, D3 : GraphBuilder.Node, O1, O2, O3> Network.Companion.create(
    port1: Port<I1, D1>,
    port2: Port<I2, D2>,
    port3: Port<I3, D3>,
    optimizer: Optimizer,
    initializer: WeightInitializer,
    block: GraphScope.(D1, D2, D3) -> GraphBuilder.Result.Sink3<O1, O2, O3>,
): Network.Src3.Sink3<I1, I2, I3, O1, O2, O3> {
    val (source1, seed1) = port1.toNode(optimizer, initializer)
    val (source2, seed2) = port2.toNode(optimizer, initializer)
    val (source3, seed3) = port3.toNode(optimizer, initializer)
    val result = GraphScope.block(seed1, seed2, seed3)
    return Network.Src3.Sink3(
        source1 = source1,
        source2 = source2,
        source3 = source3,
        graph = result.nodes,
        sink1 = result.sink1,
        sink2 = result.sink2,
        sink3 = result.sink3,
        optimizer = optimizer,
        initializer = initializer,
    )
}

sealed interface Port<T, D : GraphBuilder.Node> {
    fun toNode(optimizer: Optimizer, initializer: WeightInitializer): Pair<Graph.Source<T>, D>

    data class D1<T>(val converter: Converter.D1<T>) : Port<T, GraphBuilder.Node.D1> {
        override fun toNode(optimizer: Optimizer, initializer: WeightInitializer): Pair<Graph.Source<T>, GraphBuilder.Node.D1> {
            val source = Graph.Source(converter = converter)
            val node = GraphBuilder.Node.D1(
                from = source.id,
                nodes = listOf(),
                optimizer = optimizer,
                initializer = initializer,
                inputI = converter.outputI,
            )
            return source to node
        }
    }

    data class D2<T>(val converter: Converter.D2<T>) : Port<T, GraphBuilder.Node.D2> {
        override fun toNode(optimizer: Optimizer, initializer: WeightInitializer): Pair<Graph.Source<T>, GraphBuilder.Node.D2> {
            val source = Graph.Source(converter = converter)
            val node = GraphBuilder.Node.D2(
                from = source.id,
                nodes = listOf(),
                optimizer = optimizer,
                initializer = initializer,
                inputI = converter.outputI,
                inputJ = converter.outputJ,
            )
            return source to node
        }
    }

    data class D3<T>(val converter: Converter.D3<T>) : Port<T, GraphBuilder.Node.D3> {
        override fun toNode(optimizer: Optimizer, initializer: WeightInitializer): Pair<Graph.Source<T>, GraphBuilder.Node.D3> {
            val source = Graph.Source(converter = converter)
            val node = GraphBuilder.Node.D3(
                from = source.id,
                nodes = listOf(),
                optimizer = optimizer,
                initializer = initializer,
                inputI = converter.outputI,
                inputJ = converter.outputJ,
                inputK = converter.outputK,
            )
            return source to node
        }
    }
}

fun <T> port(converter: Converter.D1<T>): Port<T, GraphBuilder.Node.D1> = Port.D1(converter)
fun <T> port(converter: Converter.D2<T>): Port<T, GraphBuilder.Node.D2> = Port.D2(converter)
fun <T> port(converter: Converter.D3<T>): Port<T, GraphBuilder.Node.D3> = Port.D3(converter)
