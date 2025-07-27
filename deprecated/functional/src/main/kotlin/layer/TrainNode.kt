package layer

import common.sigmoid
import common.step

sealed interface TrainNode {

    /**
     * 誤差逆伝播時に使用する変数
     * この値に前の層の出力yを掛けてやることで傾きが得られる
     */
    val delta: Double
    val id: String
    val activationFunction: (Double) -> Double

    /**
     * v: Nodeに入力された値（重みを掛けて和をとった値）
     * y: 出力した値（活性化関数適用後）
     * t: 教師信号
     */
    class OutputNode(
        val v: Double,
        val y: Double,
        val t: Double,
        override val id: String,
        override val activationFunction: (Double) -> Double,
    ) : TrainNode {
        override val delta: Double by lazy { (y - t) * sigmoid(v) * (1 - sigmoid(v)) }
        override fun fixWeight(): TrainNode = this
        override fun toList(): List<List<Pair<Double, TrainNode>>> = listOf(listOf())
    }

    /**
     * v: Nodeに入力された値（重みを掛けて和をとった値）
     * y: 出力した値（活性化関数適用後）
     * after: 次の層のノードと重み
     * rate: 学習率
     */
    class NormalNode(
        val v: Double,
        val y: Double,
        val after: List<Pair<Double, TrainNode>>,
        val rate: Double,
        override val id: String,
        override val activationFunction: (Double) -> Double,
    ) : TrainNode {
        override val delta: Double by lazy { (step(v) * after.sumOf { (weight, node) -> node.delta * weight }) }

        override fun fixWeight(): TrainNode =
            after
                .map { (weight, node) -> (weight - rate * node.delta * y) to node.fixWeight() }
                .let { NormalNode(v, y, it, rate, id, activationFunction) }

        override fun toList(): List<List<Pair<Double, TrainNode>>> =
            after.flatMap { (weight, node) -> node.toList().map { listOf((weight to node)) + it } }
    }

    /**
     * 重みを更新した後のTrainNodeを取得する関数
     */
    fun fixWeight(): TrainNode

    /**
     * ツリー構造状に保存しているnodeをList型に変換する
     */
    fun toList(): List<List<Pair<Double, TrainNode>>>
}
