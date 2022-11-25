package layer

import common.Memoizer
import java.util.UUID
import kotlin.random.Random

class Node(
    val before: List<Pair<Node, Double>>?,
    val activationFunction: (Double) -> Double,
    val id: String = UUID.randomUUID().toString(),
) {
    /**
     * getVY関数は何度も呼び出されるためキャッシュとして残しておく
     */
    private val getVYMemoizer = Memoizer<List<Double>, Pair<Double, Double>>()

    /**
     * ノードに入ってきた信号及びノードの出力信号を取得する
     */
    fun getVY(input: List<Double>): Pair<Double, Double> =
        getVYMemoizer(input) {
            when {
                // 入力層だった場合、ノードに入ってきた値は0として扱う
                before == null -> 0.0 to input[id.toInt() - 33]

                // 入力層の次の層の場合は、入力層の合計に重みを掛けたものを入力として扱う
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

    /**
     * ツリー構造状に保存しているnodeをList型に変換する
     */
    fun toList(): List<List<Pair<Node, Double>>> =
        before?.flatMap { (node, weight) ->
            node.toList().map { it + (node to weight) }
        } ?: listOf(listOf())

    companion object {

        /**
         * 入力層作成用Factory
         * id + 33と入力位置が対応するようにする
         */
        fun createInputNodes(
            size: Int,
            activationFunction: (Double) -> Double,
        ): List<Node> = List(size) { index ->
            Node(
                before = null,
                activationFunction = activationFunction,
                id = "${index + 33}",
            )
        }

        /**
         * 中間層作成用Factory
         */
        fun createCenterNodes(
            before: List<Node>,
            size: Int,
            activationFunction: (Double) -> Double,
            from: Double = -1.0,
            to: Double = 1.0,
            random: Random = Random,
        ): List<Node> = List(size) {
            Node(
                before = before.map { it to random.nextDouble(from, to) },
                activationFunction = activationFunction,
            )
        }

        /**
         * 出力層作成用Factory
         * idとlabelが対応するようにする
         */
        fun createOutputNodes(
            before: List<Node>,
            size: Int,
            activationFunction: (Double) -> Double,
            from: Double = -1.0,
            to: Double = 1.0,
            random: Random = Random,
        ): List<Node> = List(size) { index ->
            Node(
                before = before.map { it to random.nextDouble(from, to) },
                activationFunction = activationFunction,
                id = index.toString(),
            )
        }
    }
}
