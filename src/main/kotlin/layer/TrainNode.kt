package layer
import sigmoid
import step

sealed interface TrainNode {
    val delta: Double
    val id: String
    val activationFunction: (Double) -> Double

    class OutputNode(
        val v: Double,
        val y: Double,
        val t: Double,
        override val id: String,
        override val activationFunction: (Double) -> Double,
    ) : TrainNode {
        override fun toString() = "OutputN(delta: $delta, v: $v, y: $y, t: $t)"
        override val delta: Double = (y - t) * sigmoid(v) * (1 - sigmoid(v))
        override fun fixWeight(): TrainNode = this
        override fun toList(): List<List<Pair<Double, TrainNode>>> = listOf(listOf())
    }

    class NormalNode(
        val v: Double,
        val y: Double,
        val nodes: List<Pair<Double, TrainNode>>,
        val rate: Double,
        override val id: String,
        override val activationFunction: (Double) -> Double,
    ) : TrainNode {
        override fun toString() = "delta: $delta, y: $y, nodes: $nodes"
        override val delta: Double =
            (step(v) * nodes.sumOf { (weight, node) -> node.delta * weight })

        override fun fixWeight(): TrainNode =
            nodes
                .map { (weight, node) -> (weight - rate * node.delta * y) to node.fixWeight() }
                .let { NormalNode(v, y, it, rate, id, activationFunction) }

        override fun toList(): List<List<Pair<Double, TrainNode>>> =
            nodes.flatMap { (weight, node) -> node.toList().map { listOf((weight to node)) + it } }
    }

    fun fixWeight(): TrainNode

    fun toList(): List<List<Pair<Double, TrainNode>>>
}

fun List<List<Pair<Double, Node>>>.toTrainNodesTree(
    input: List<Double>,
    label: Int,
    rate: Double,
): List<Pair<Double, TrainNode>> = when {
    this.all { it.size == 1 } -> this.map {
        val (weight, node) = it.first()
        val (v, y) = node.getVY(input)
        val error = if (node.id == label.toString()) 1.0 else 0.0
        weight to TrainNode.OutputNode(v = v, y = y, t = error, node.id, node.activationFunction)
    }
    else ->
        this
            .groupBy { it.first().second.id }
            .mapKeys { it.value.first().first() }
            .mapValues { (_, value) -> value.map { it.drop(1) }.toTrainNodesTree(input, label, rate) }
            .map {
                val (v, y) = it.key.second.getVY(input)
                it.key.first to TrainNode.NormalNode(
                    v = v,
                    y = y,
                    nodes = it.value,
                    rate = rate,
                    it.key.second.id,
                    it.key.second.activationFunction,
                )
            }
}

fun List<List<Pair<TrainNode, Double>>>.toNodesTree(): List<Pair<Node, Double>> = when {
    this.all { it.size == 1 } -> this.map {
        val (node, weight) = it.first()
        Node(null, node.activationFunction, id = node.id) to weight
    }
    else ->
        this
            .groupBy { it.last().first.id }
            .mapKeys { it.value.last().last() }
            .mapValues { (_, value) -> value.map { it.dropLast(1) }.toNodesTree() }
            .map {
                Node(it.value, it.key.first.activationFunction, id = it.key.first.id) to it.key.second
            }
}
