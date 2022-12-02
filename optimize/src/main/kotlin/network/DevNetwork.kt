@file:Suppress("DuplicatedCode")

package network

import common.add
import common.conv
import common.convA
import common.convArray
import common.foreachDownIndexed
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
    private val windowedLayers = layers.windowed(2) { (before, after) -> before to after }

    /**
     * 推定を行う関数
     * inputからラベル値を返す
     */
    fun expect(input: List<Double>): Int {
        val output = forward(input.toTypedArray())
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
        val output = forward(input.toTypedArray())
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
        val output: Array<Array<Any>> = Array(layers.size) { Array(layers[it].size) {  } }

        measureNanoTime {
            // 入力を第1層の出力とする
            output[0] = input

            windowedLayers
                .mapIndexed { layer, (before, after) ->
                    // 後ろのレイヤーに合わせて処理を変える
                    when (after.type) {
                        // Input層は最初以外ないはずだからエラーとなる
                        is LayerType.Input -> throw Exception()
                        // 全結合層
                        is LayerType.MatMul ->
                            matMul(
                                input = when (before.type) {
                                    // 入力層の次に全結合なのでList<Double>が来るはず
                                    is LayerType.Input -> output[layer] as Array<Double>
                                    is LayerType.MatMul -> output[layer] as Array<Double>
                                    is LayerType.Conv ->
                                        (output[layer] as Array<Array<Array<Double>>>)
                                            .map { it.flatten() }
                                            .flatten()
                                            .toTypedArray()
                                },
                                layer = layer,
                                before = before,
                                after = after,
                            ).toTypedArray()
                        is LayerType.Conv ->
                            conv(
                                // TODO: 全結合の次にConvがくる場合を想定していない
                                input = output[layer] as Array<Array<Array<Double>>>,
                                layer = layer,
                                before = before,
                                after = after,
                            )
                    }.let { output[layer + 1] = it as Array<Any> }
                }
        }.let { println("forward: $it") }
        return output
    }

    // 全結合の計算結果を出す
    private fun matMul(
        input: Array<Double>,
        layer: Int,
        before: LayerConfig,
        after: LayerConfig,
    ): List<Double> {
        val out = mutableListOf<Double>()
        measureNanoTime {
            for (a in 0 until after.size) {
                after.activationFunction((0 until before.size).sumOf { b -> input[b] * weights[layer][b][a] as Double })
                    .let { out.add(it) }
            }
        }.let { println("matMul: $it") }
        return out
    }

    /**
     * 畳み込みの結果を出す
     */
    private fun conv(
        input: Array<Array<Array<Double>>>,
        layer: Int,
        before: LayerConfig,
        after: LayerConfig,
    ): Array<Array<Array<Double>>> {
        val outputSize = input.first().size - (weights[layer][0][0] as Array<Array<Double>>).size + 1
        val output: Array<Array<Array<Double>>> = Array(after.size) { Array(outputSize) { Array(outputSize) { 0.0 } } }
        measureNanoTime {
            for (a in 0 until after.size) {
                for (b in 0 until before.size) {
                    output[a].add(input[b].convArray(weights[layer][b][a] as Array<Array<Double>>))
                }
            }
        }.let { println("conv: $it") }
        return output
    }

    private fun pool(
        input: List<List<List<Double>>>,
    ): List<List<List<Double>>> =
        input.map { i ->
            (0 until i.size - 1).map { j ->
                (0 until i.size - 1).map { k ->
                    maxOf(i[j][k], i[j][k + 1], i[j + 1][k], i[j + 1][k + 1])
                }
            }
        }

    /**
     * 誤差逆伝搬のためのdeltaを取得する関数
     */
    private fun calcDelta(output: Array<Array<Any>>, label: Int): List<List<Double>> {
        val delta = mutableListOf<List<Double>>()

        measureNanoTime {
            // 最終層のDeltaを計算
            (0 until layers.last().size).map {
                val y = output[layers.size - 1][it] as Double
                (y - if (it == label) 0.9 else 0.1) * (1 - y) * y
            }.let { delta.add(it) }

            windowedLayers
                .foreachDownIndexed { index, (before, after) ->
                    (0 until before.size).map { b ->
                        when (before.type) {
                            // Input層は計算する必要がないため除外
                            is LayerType.Input -> 0.0
                            is LayerType.MatMul -> step(output[index][b] as Double) *
                                    (0 until after.size).sumOf { a -> delta[0][a] * weights[index][b][a] as Double }
                            is LayerType.Conv -> {
                                when (after.type) {
                                    is LayerType.Input, LayerType.MatMul ->
                                        (output[index][b] as Array<Array<Double>>)
                                            .sumOf {
                                                it.sumOf { x ->
                                                    step(x) * (0 until after.size).sumOf { a -> delta[0][a] * weights[index][b][a] as Double }
                                                }
                                            }
                                    is LayerType.Conv ->
                                        (output[index][b] as Array<Array<Double>>)
                                            .sumOf { row ->
                                                row.sumOf { x ->
                                                    step(x) * (0 until after.size).sumOf { a ->
                                                        (weights[index][b][a] as Array<Array<Double>>)
                                                            .sumOf { weightRow -> weightRow.sumOf { delta[0][a] * it } }
                                                    }
                                                }
                                            }
                                }
                            }
                        }
                    }.let { delta.add(0, it) }
                }
        }.let { println("calcDelta: $it") }

        return delta
    }

    /**
     * 誤差逆伝搬を行う関数
     */
    private fun backward(
        output: Array<Array<Any>>,
        delta: List<List<Double>>,
    ) {
        measureNanoTime {
            windowedLayers
                .mapIndexed { index, (before, after) ->
                    (0 until before.size).forEach { b ->
                        (0 until after.size).forEach { a ->
                            weights[index][b][a] = when (after.type) {
                                // Input層には更新する値はないはずだからエラーとなる
                                is LayerType.Input -> throw Exception()
                                is LayerType.MatMul ->
                                    weights[index][b][a] as Double - rate * delta[index + 1][a] *
                                            when (before.type) {
                                                is LayerType.Input, LayerType.MatMul ->
                                                    output[index][b] as Double
                                                is LayerType.Conv ->
                                                    (output[index] as Array<Array<Array<Double>>>)
                                                        .map { it.flatten() }
                                                        .flatten()[b]
                                            }
                                is LayerType.Conv ->
                                    (weights[index][b][a] as Array<Array<Double>>).map { weightMatrix ->
                                        weightMatrix.map { weight ->
                                            weight - (output[index][b] as Array<Array<Double>>)
                                                .sumOf { row ->
                                                    row.sumOf { column ->
                                                        column * rate * delta[index + 1][a]
                                                    }
                                                }
                                        }.toTypedArray()
                                    }.toTypedArray()
                            }
                        }
                    }
                }
        }.let { println("backward: $it") }
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
