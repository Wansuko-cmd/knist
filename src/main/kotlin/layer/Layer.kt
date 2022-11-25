package layer

import Memoizer
import TrainNode
import relu
import sigmoid
import toNodesTree
import toTrainNodesTree
import java.util.UUID
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
     * その形で逆伝播の計算を行う
     * 逆伝播の計算を行い重みの更新を行なった後はもう一度ツリーを逆にし、Layerにする
     */
    fun train(input: List<Double>, label: Int): Layer =
        output
            .fromNodeTreeToList()
            .slideWeightToLeft()
            .toTrainNodesTree(input, label, rate)
            .map { it.copy(second = (it.second as TrainNode.NormalN).fixWeight()) }
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
     */
    private fun List<List<Pair<Node, Double>>>.slideWeightToLeft(): List<List<Pair<Double, Node>>> =
        this.map { node ->
            listOf(Double.NEGATIVE_INFINITY to node.first().first) +
                node.windowed(2) { (left, right) -> left.second to right.first }
        }

    /**
     * 学習用の木構造を元のNode Treeに戻すためにまずリスト化する
     */
    private fun List<Pair<Double, TrainNode>>.fromTrainTreeToList(): List<List<Pair<Double, TrainNode>>> =
        this.flatMap { node ->
            node.second.toList().map { listOf(Double.NEGATIVE_INFINITY to node.second) + it }
        }

    /**
     * Nodeでの[Node, Double]の形に戻す
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
            random: Random,
        ): Layer {
            val inputNode = List(input) { Node.input(::relu, it) }
            val centerNode = center.fold(inputNode) { acc, i ->
                List(i) {
                    Node.create(acc, f = ::relu, random = random)
                }
            }
            val outputNode = List(output) { index ->
                Node.create(centerNode, id = index.toString(), f = ::sigmoid, random = random)
            }
            return Layer(outputNode, rate)
        }
    }
}

class Node(
    val before: List<Pair<Node, Double>>?,
    val activationFunction: (Double) -> Double,
    val id: String = UUID.randomUUID().toString(),
) {
    override fun toString() = before.toString()

    private val memoizer = Memoizer<List<Double>, Pair<Double, Double>>()

    /**
     * ノードに入ってきた信号及び出力信号を取得する
     */
    fun getVY(input: List<Double>): Pair<Double, Double> =
        memoizer(input) {
            when {
                before == null -> 0.0 to input[id.toInt() - 33]
                before.map { it.first.before }.all { it == null } -> {
                    val v = before.zip(input) { (_, weight), input -> input * weight }.sum()
                    v to activationFunction(v)
                }
                else -> {
                    before
                        .sumOf { (node, weight) -> node.getVY(input).second * weight }
                        .let { it to activationFunction(it) }
                }
            }
        }

    fun toList(): List<List<Pair<Node, Double>>> =
        before?.flatMap { (node, weight) ->
            node.toList().map { it + (node to weight) }
        } ?: listOf(listOf())

    companion object {
        fun create(
            node: List<Node>,
            id: String = UUID.randomUUID().toString(),
            f: (Double) -> Double,
            random: Random,
        ): Node = Node(
            before = node.map { it to random.nextDouble(from = -1.0, until = 1.0) },
            activationFunction = f,
            id = id,
        )

        fun create(f: (Double) -> Double, id: String) = Node(null, activationFunction = f, id)

        fun input(f: (Double) -> Double, index: Int) = Node(
            null,
            activationFunction = f,
            id = "${index + 33}",
        )
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
