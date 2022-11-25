package layer

import relu
import sigmoid
import kotlin.random.Random

class Layer(
    private val output: List<Node>,
    private val rate: Double,
) {
    fun forward(input: List<Double>) = output
        .map { it.getVY(input).second }
        .maxIndex()

    /**
     * 学習を行った後のLayerを返す関数
     * * 引数
     * input: 入力データ
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
            .map { it.copy(second = (it.second as TrainNode.NormalNode).fixWeight()) }
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

fun <T : Comparable<T>> List<T>.maxIndex(): Int =
    this.foldIndexed(null) { index: Int, acc: Pair<Int, T>?, element: T ->
        when {
            acc == null -> index to element
            acc.second > element -> acc
            else -> index to element
        }
    }?.first ?: throw Exception()
