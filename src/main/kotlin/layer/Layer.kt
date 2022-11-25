package layer

import common.maxIndex
import common.relu
import common.sigmoid
import kotlin.random.Random

class Layer private constructor(
    private val output: List<Node>,
    private val rate: Double,
) {
    /**
     * 入力値を元にラベルを予想する関数
     * 学習は行わない
     */
    fun forward(input: List<Double>): Int = output
        .map { it.getVY(input).second }
        .maxIndex()

    /**
     * 学習を行った後のLayerを返す関数
     * * 引数
     * input: 入力データ(一つのみ)
     * label: 正解ラベル
     * * 返り値
     * 引数の値を元に学習を行なった後のLayer
     * * 解説
     * Nodesは順伝播を計算するのに長けたツリー状になっている
     * しかし、逆伝播を計算することはできない
     * そこで、一度ツリーをリスト状にした上で逆ツリー状にする
     * 逆ツリー状にすることで逆伝播の計算を行うことができるようになる
     * 逆伝播の計算を行い重みの更新を行なった後はもう一度ツリーを逆にし、Layerにする
     */
    fun train(input: List<Double>, label: Int): Layer =
        output
            .fromNodeTreeToList()
            .slideWeightToLeft()
            .toTrainNodesTree(input, label, rate)
            .map { (weight, node) -> weight to (node as TrainNode.NormalNode).fixWeight() }
            .fromTrainTreeToList()
            .slideWeightToRight()
            .toNodesTree()
            .let { nodes -> Layer(nodes.map { it.first }, rate) }

    /**
     * ツリー状になっているニューラルネットワークをリスト化
     * ノードの数より重みの数の方が少ないため、数合わせのためにNEGATIVE_INFINITYを用いる（使うことはないため無視で良い）
     */
    private fun List<Node>.fromNodeTreeToList() =
        this.flatMap { node -> node.toList().map { it + (node to Double.NEGATIVE_INFINITY) } }

    /**
     * Nodeでは[Node, Double]の形で保管しているが、逆伝播を行うときは[Double, Node]の方が望ましい
     * ノードの数より重みの数の方が少ないため、数合わせのためにNEGATIVE_INFINITYを用いる（使うことはないため無視で良い）
     */
    private fun List<List<Pair<Node, Double>>>.slideWeightToLeft(): List<List<Pair<Double, Node>>> =
        this.map { node ->
            listOf(Double.NEGATIVE_INFINITY to node.first().first) +
                node.windowed(2) { (left, right) -> left.second to right.first }
        }

    /**
     * リスト状にしたノードをTrainNodesの木構造に変換するための関数
     */
    private fun List<List<Pair<Double, Node>>>.toTrainNodesTree(
        input: List<Double>,
        label: Int,
        rate: Double,
    ): List<Pair<Double, TrainNode>> = when {
        // 次の要素がなければ出力層として処理
        this.all { it.size == 1 } -> this.map {
            val (weight, node) = it.first()
            val (v, y) = node.getVY(input)
            val error = if (node.isCorrespondOutputNode(label)) 1.0 else 0.0
            weight to TrainNode.OutputNode(v = v, y = y, t = error, node.id, node.activationFunction)
        }
        else ->
            this
                // 先頭の同じIDの要素を括り出し
                .groupBy { it.first().second.id }
                .mapKeys { it.value.first().first() }
                .mapValues { (_, value) -> value.map { it.drop(1) }.toTrainNodesTree(input, label, rate) }
                .map {
                    val (v, y) = it.key.second.getVY(input)
                    it.key.first to TrainNode.NormalNode(
                        v = v,
                        y = y,
                        after = it.value,
                        rate = rate,
                        it.key.second.id,
                        it.key.second.activationFunction,
                    )
                }
    }

    /**
     * 学習用の木構造を元のNode Treeに戻すためにまずリスト化する
     * ノードの数より重みの数の方が少ないため、数合わせのためにNEGATIVE_INFINITYを用いる（使うことはないため無視で良い）
     */
    private fun List<Pair<Double, TrainNode>>.fromTrainTreeToList(): List<List<Pair<Double, TrainNode>>> =
        this.flatMap { node ->
            node.second.toList().map { listOf(Double.NEGATIVE_INFINITY to node.second) + it }
        }

    /**
     * Nodeでの[Node, Double]の形に戻す
     * ノードの数より重みの数の方が少ないため、数合わせのためにNEGATIVE_INFINITYを用いる（使うことはないため無視で良い）
     */
    private fun List<List<Pair<Double, TrainNode>>>.slideWeightToRight(): List<List<Pair<TrainNode, Double>>> =
        this.map { node ->
            node.windowed(2) { (left, right) -> left.second to right.first } +
                (node.last().second to Double.NEGATIVE_INFINITY)
        }

    /**
     * リスト状にしたノードをNodesの木構造に変換するための関数
     */
    private fun List<List<Pair<TrainNode, Double>>>.toNodesTree(): List<Pair<Node, Double>> = when {
        // 次の要素がなければ入力層として処理
        this.all { it.size == 1 } -> this.map {
            val (node, weight) = it.first()
            Node.reconstruct(
                null,
                activationFunction = node.activationFunction,
                id = node.id,
            ) to weight
        }
        else ->
            this
                // 末尾の同じIDの要素を括り出し
                .groupBy { it.last().first.id }
                .mapKeys { it.value.last().last() }
                .mapValues { (_, value) -> value.map { it.dropLast(1) }.toNodesTree() }
                .map {
                    Node.reconstruct(
                        before = it.value,
                        activationFunction = it.key.first.activationFunction,
                        id = it.key.first.id,
                    ) to it.key.second
                }
    }

    companion object {
        fun create(
            input: Int,
            center: List<Int>,
            output: Int,
            rate: Double,
            random: Random = Random,
        ): Layer {
            val inputNodes = Node.createInputNodes(
                size = input,
                activationFunction = ::relu,
            )
            val centerNodes = center.fold(inputNodes) { acc, size ->
                Node.createCenterNodes(
                    before = acc,
                    size = size,
                    activationFunction = ::relu,
                    random = random,
                )
            }
            val outputNodes = Node.createOutputNodes(
                before = centerNodes,
                size = output,
                activationFunction = ::sigmoid,
                random = random,
            )
            return Layer(outputNodes, rate)
        }
    }
}
