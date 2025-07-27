@file:Suppress("DuplicatedCode")

package network

import common.add
import common.convArray
import common.maxIndex
import common.step
import kotlin.random.Random
import kotlin.system.measureNanoTime

/**
 * layers: 各層の情報を保持する
 * Int -> 層のニューロン数
 *
 * weight: 重みを保持する
 * List[層][前のニューロン][後ろのニューロン]
 * 0からカウントを開始する
 *
 * rate: 学習率
 */
class DevNetwork private constructor(
    private val layers: List<LayerConfig>,
    private val weights: Array<Array<Array<Any>>>,
    private val rate: Double,
) {

    /**
     * 推定を行う関数
     * inputからラベル値を返す
     */
    fun expect(input: List<Double>): Int {
        val output = forward(input.toTypedArray() as Array<Any>)
        return (0 until layers.last().size).map { output[layers.size - 1][it] as Double }.maxIndex()
    }

    fun expects(input: List<List<List<Double>>>): Int {
        val i = input.map { it.map { it.toTypedArray() }.toTypedArray() }.toTypedArray()
        val output = forward(i as Array<Any>)
        return (0 until layers.last().size).map { output[layers.size - 1][it] as Double }.maxIndex()
    }

    /**
     * 学習を行う関数
     * inputの値とラベルを用いてSGD学習を行う
     */
    fun train(input: List<Double>, label: Int) {
        val output = forward(input.toTypedArray() as Array<Any>)
        backward(output, calcDelta(output, label))
    }

    fun trains(input: List<List<List<Double>>>, label: Int) {
        val i = input.map { it.map { it.toTypedArray() }.toTypedArray() }.toTypedArray()
        val output = forward(i as Array<Any>)
        backward(output, calcDelta(output, label))
    }

    /**
     * 順伝搬を行う関数
     */
    private fun forward(input: Array<Any>): Array<Array<Any>> {
        val output: Array<Array<Any>> = Array(layers.size) { Array(layers[it].size) { } }

        measureNanoTime {
            // 入力を第1層の出力とする
            output[0] = input

            for (i in 0 until layers.size - 1) {
                output[i + 1] = when (layers[i + 1].type) {
                    is LayerType.Input -> throw Exception()
                    is LayerType.MatMul -> {
                        matMul(
                            input = when (layers[i].type) {
                                // 入力層の次に全結合なのでArray<Double>が来るはず
                                is LayerType.Input, LayerType.MatMul ->
                                    output[i] as Array<Double>
                                is LayerType.Conv ->
                                    (output[i] as Array<Array<Array<Double>>>)
                                        .map { it.flatten() }
                                        .flatten()
                                        .toTypedArray()
                            },
                            layer = i,
                        )
                    }
                    is LayerType.Conv -> {
                        conv(
                            // TODO: 全結合の次にConvがくる場合を想定していない
                            input = output[i] as Array<Array<Array<Double>>>,
                            layer = i,
                        ).also { pool(it) }
                    }
                } as Array<Any>
            }
        }
//            .let { println("forward: $it") }
        return output
    }

    // 全結合の計算結果を出す
    private fun matMul(
        input: Array<Double>,
        layer: Int,
    ): Array<Double> {
        val out = Array(layers[layer + 1].size) { 0.0 }
        measureNanoTime {
            for (a in 0 until layers[layer + 1].size) {
                layers[layer + 1].activationFunction((0 until layers[layer].size).sumOf { b -> input[b] * weights[layer][b][a] as Double })
                    .let { out[a] = it }
            }
        }
//            .let { println("matMul: $it") }
        return out
    }

    /**
     * 畳み込みの結果を出す
     */
    private fun conv(
        input: Array<Array<Array<Double>>>,
        layer: Int,
    ): Array<Array<Array<Double>>> {
        val outputSize = input.first().size - (weights[layer][0][0] as Array<Array<Double>>).size + 1
        val output: Array<Array<Array<Double>>> = Array(layers[layer + 1].size) { Array(outputSize) { Array(outputSize) { 0.0 } } }
        measureNanoTime {
            for (a in 0 until layers[layer + 1].size) {
                for (b in 0 until layers[layer].size) {
                    output[a].add(input[b].convArray(weights[layer][b][a] as Array<Array<Double>>, layers[layer + 1].activationFunction))
                }
            }
        }
//            .let { println("conv: $it") }
        return output
    }

    private fun pool(
        input: Array<Array<Array<Double>>>,
    ): Array<Array<Array<Double>>> =
        input.map { i ->
            (0 until i.size - 1).map { j ->
                (0 until i.size - 1).map { k ->
                    maxOf(i[j][k], i[j][k + 1], i[j + 1][k], i[j + 1][k + 1])
                }.toTypedArray()
            }.toTypedArray()
        }.toTypedArray()

    /**
     * 誤差逆伝搬のためのdeltaを取得する関数
     */
    private fun calcDelta(output: Array<Array<Any>>, label: Int): Array<Array<Double>> {
        val delta = Array(layers.size) { Array(layers[it].size) { 0.0 } }

        measureNanoTime {
            // 最終層のDeltaを計算
            for (i in 0 until layers.last().size) {
                val y = output[layers.size - 1][i] as Double
                delta[layers.size - 1][i] = (y - if (i == label) 0.9 else 0.1) * (1 - y) * y
            }
            for (i in layers.size - 2 downTo 1) {
                when (layers[i].type) {
                    is LayerType.Input -> break
                    is LayerType.MatMul -> {
                        for (b in 0 until layers[i].size) {
                            delta[i][b] = step(output[i][b] as Double) *
                                (0 until layers[i + 1].size).sumOf { a -> delta[i + 1][a] * weights[i][b][a] as Double }
                        }
                    }
                    is LayerType.Conv -> {
                        when (layers[i + 1].type) {
                            is LayerType.Input, LayerType.MatMul -> {
                                for (b in 0 until layers[i].size) {
                                    delta[i][b] = (output[i][b] as Array<Array<Double>>)
                                        .sumOf {
                                            it.sumOf { x ->
                                                step(x) * (0 until layers[i + 1].size).sumOf { a -> delta[i + 1][a] * weights[i][b][a] as Double }
                                            }
                                        }
                                }
                            }
                            is LayerType.Conv -> {
                                for (b in 0 until layers[i].size) {
                                    delta[i][b] = (output[i][b] as Array<Array<Double>>)
                                        .sumOf { row ->
                                            row.sumOf { x ->
                                                step(x) * (0 until layers[i + 1].size).sumOf { a ->
                                                    (weights[i][b][a] as Array<Array<Double>>)
                                                        .sumOf { weightRow -> weightRow.sumOf { delta[i + 1][a] * it } }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
//            .let { println("calcDelta: $it") }

        return delta
    }

    /**
     * 誤差逆伝搬を行う関数
     */
    private fun backward(
        output: Array<Array<Any>>,
        delta: Array<Array<Double>>,
    ) {
        measureNanoTime {
            for (i in 0 until layers.size - 1) {
                when (layers[i + 1].type) {
                    is LayerType.Input -> throw Exception()
                    is LayerType.MatMul -> {
                        val out = when (layers[i].type) {
                            is LayerType.Input, LayerType.MatMul ->
                                output[i] as Array<Double>
                            is LayerType.Conv ->
                                (output[i] as Array<Array<Array<Double>>>)
                                    .map { it.flatten() }
                                    .flatten()
                                    .toTypedArray()
                        }
                        for (b in 0 until layers[i].size) {
                            for (a in 0 until layers[i + 1].size) {
                                weights[i][b][a] = (weights[i][b][a] as Double) - rate * delta[i + 1][a] * out[b]
                            }
                        }
                    }
                    is LayerType.Conv -> {
                        for (b in 0 until layers[i].size) {
                            for (a in 0 until layers[i + 1].size) {
                                val w = (weights[i][b][a] as Array<Array<Double>>)

                                var d = 0.0
                                val del = delta[i + 1][a]
                                val ou = output[i][b] as Array<Array<Double>>
                                for (u in ou.indices) {
                                    for (v in ou[u].indices) {
                                        d += ou[u][v] * rate * del
                                    }
                                }

                                for (u in w.indices) {
                                    for (v in w[u].indices) {
                                        w[u][v] -= d
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//            .let { println("backward: $it") }
    }

    companion object {
        fun create(
            input: InputConfig,
            layerConfigs: List<LayerConfig>,
            random: Random,
            rate: Double,
        ): DevNetwork {
            val layers = listOf(input.toLayoutConfig()) + layerConfigs
            val weights: Array<Array<Array<Any>>> = Array(layerConfigs.size) { i ->
                Array(layers[i].size) { Array(layers[i + 1].size) {} }
            }
            layers
                .windowed(2) { (before, after) -> before to after }
                .mapIndexed { index, (before, after) ->
                    for (b in 0 until before.size) {
                        for (a in 0 until after.size) {
                            when (after.type) {
                                is LayerType.Input -> throw Exception()
                                is LayerType.MatMul -> random.nextDouble(from = -1.0, until = 1.0)
                                is LayerType.Conv -> Array(5) { Array(5) { random.nextDouble(-1.0, 1.0) } }
                            }.let { weights[index][b][a] = it }
                        }
                    }
                }
            return DevNetwork(layers, weights, rate)
        }
    }
}

data class LayerConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
    val type: LayerType,
)

data class InputConfig(val size: Int) {
    fun toLayoutConfig() = LayerConfig(size, { it }, LayerType.Input)
}

sealed class LayerType {
    internal object Input : LayerType()
    object MatMul : LayerType()
    object Conv : LayerType()
}
