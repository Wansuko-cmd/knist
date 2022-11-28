@file:Suppress("DuplicatedCode")

package network

import common.mapDownIndexed
import common.maxIndex
import common.relu
import common.sigmoid
import common.step
import kotlin.random.Random

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
    private val weights: List<List<MutableList<Double>>>,
    private val rate: Double,
) {
    private val windowedLayers = layers.windowed(2) { (before, after) -> before to after }

    /**
     * 推定を行う関数
     * inputからラベル値を返す
     */
    fun expect(input: List<Double>): Int {
        val output = forward(input)
        return (0 until layers.last().size).map { output[layers.size - 1][it] }.maxIndex()
    }

    /**
     * 学習を行う関数
     * inputの値とラベルを用いてSGD学習を行う
     */
    fun train(input: List<Double>, label: Int) {
        val output = forward(input)
        backward(output, calcDelta(output, label))
    }

    /**
     * 順伝搬を行う関数
     */
    private fun forward(input: List<Double>): List<List<Double>> {
        val output = mutableListOf<List<Double>>()
        output.add(input)

        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until after.size).map { a ->
                    after.activationFunction((0 until before.size).sumOf { b -> output[index][b] * weights[index][b][a] })
                }.let { output.add(it) }
            }
        return output
    }

    /**
     * 誤差逆伝搬を行う関数
     */
    private fun backward(
        output: List<List<Double>>,
        delta: List<List<Double>>,
    ) {
        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until before.size).forEach { b ->
                    (0 until after.size).forEach { a ->
                        weights[index][b][a] =
                            weights[index][b][a] - rate * delta[index + 1][a] * output[index][b]
                    }
                }
            }
    }

    /**
     * 誤差逆伝搬のためのdeltaを取得する関数
     */
    private fun calcDelta(output: List<List<Double>>, label: Int): List<List<Double>> {
        val delta = mutableListOf<List<Double>>()
        (0 until layers.last().size).map {
            val y = output[layers.size - 1][it]
            (y - if (it == label) 0.9 else 0.1) * (1 - y) * y
        }.let { delta.add(it) }
        windowedLayers
            .mapDownIndexed { index, (before, after) ->
                (0 until before.size).map { b ->
                    step(output[index][b]) *
                        (0 until after.size).sumOf { a -> delta[0][a] * weights[index][b][a] }
                }.let { delta.add(0, it) }
            }
        return delta
    }

    companion object {
        fun create(layers: List<LayerConfig>, random: Random, rate: Double): DevNetwork {
            val weights = mutableListOf<List<MutableList<Double>>>()
            layers
                .windowed(2) { (before, after) -> before to after }
                .map { (before, after) ->
                    (0 until before.size).map {
                        (0 until after.size).map {
                            random.nextDouble(from = -1.0, until = 1.0)
                        }.toMutableList()
                    }.let { weights.add(it) }
                }

            return DevNetwork(layers, weights, rate)
        }
    }
}

data class LayerConfig(
    val size: Int,
    val activationFunction: (Double) -> Double,
)
