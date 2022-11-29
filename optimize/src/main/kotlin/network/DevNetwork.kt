@file:Suppress("DuplicatedCode")

package network

import common.conv
import common.mapDownIndexed
import common.maxIndex
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
    private val weights: List<List<MutableList<Any>>>,
    private val rate: Double,
) {
    private val windowedLayers = layers.windowed(2) { (before, after) -> before to after }

    /**
     * 推定を行う関数
     * inputからラベル値を返す
     */
    fun expect(input: List<Double>): Int {
        val output = forward(input)
        return (0 until layers.last().size).map { output[layers.size - 1][it] as Double }.maxIndex()
    }

    fun expects(input: List<List<List<Double>>>): Int {
        val output = forward(input)
        return (0 until layers.last().size).map { output[layers.size - 1][it] as Double }.maxIndex()
    }

    /**
     * 学習を行う関数
     * inputの値とラベルを用いてSGD学習を行う
     */
    fun train(input: List<Double>, label: Int) {
        val output = forward(input)
        backward(output, calcDelta(output, label))
    }

    fun trains(input: List<List<List<Double>>>, label: Int) {
        val output = forward(input)
        backward(output, calcDelta(output, label))
    }

    /**
     * 順伝搬を行う関数
     */
    private fun forward(input: List<Any>): List<List<Any>> {
        val output = mutableListOf<List<Any>>()
        output.add(input)

        windowedLayers
            .mapIndexed { layer, (before, after) ->
                when (after.type) {
                    is LayerType.MatMul -> {
                        val o: List<Double> =
                            if (before.type is LayerType.Conv) {
                                (output[layer] as List<List<List<Double>>>).map { it.flatten() }.flatten()
                            } else output[layer] as List<Double>
                        matMul(input = o, layer = layer, before = before, after = after)
                    }
                    is LayerType.Conv ->
                        conv(input = output[layer] as List<List<List<Double>>>, layer = layer, before = before, after = after)
                }.let { output.add(it) }
            }
        return output
    }

    private fun matMul(
        input: List<Double>,
        layer: Int,
        before: LayerConfig,
        after: LayerConfig,
    ): List<Double> =
        (0 until after.size).map { a ->
            after.activationFunction((0 until before.size).sumOf { b -> input[b] * weights[layer][b][a] as Double })
        }

    private fun conv(
        input: List<List<List<Double>>>,
        layer: Int,
        before: LayerConfig,
        after: LayerConfig,
    ): List<List<List<Double>>> =
        (0 until after.size).map { a ->
            (0 until before.size).map { b -> input[b].conv(weights[layer][b][a] as List<List<Double>>, after.activationFunction) }.flatten()
        }

    /**
     * 誤差逆伝搬を行う関数
     */
    private fun backward(
        output: List<List<Any>>,
        delta: List<List<Double>>,
    ) {
        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until before.size).forEach { b ->
                    (0 until after.size).forEach { a ->
                        weights[index][b][a] = when (after.type) {
                            is LayerType.MatMul ->
                                weights[index][b][a] as Double - rate * delta[index + 1][a] *
                                    if (output[index][b] is Double) output[index][b] as Double
                                    else (output[index] as List<List<List<Double>>>).map { it.flatten() }.flatten()[b]
                            is LayerType.Conv ->
                                (weights[index][b][a] as List<List<Double>>).map {
                                    it.map { w ->
                                        w - (output[index][b] as List<List<Double>>).sumOf {
                                            it.sumOf {
                                                it * rate * delta[index + 1][a]
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }
            }
    }

    /**
     * 誤差逆伝搬のためのdeltaを取得する関数
     */
    private fun calcDelta(output: List<List<Any>>, label: Int): List<List<Double>> {
        val delta = mutableListOf<List<Double>>()
        (0 until layers.last().size).map {
            val y = output[layers.size - 1][it] as Double
            (y - if (it == label) 0.9 else 0.1) * (1 - y) * y
        }.let { delta.add(it) }
        windowedLayers
            .mapDownIndexed { index, (before, after) ->
                if (index == 0) {
                    delta.add(0, listOf())
                    return@mapDownIndexed
                }
                (0 until before.size).map { b ->
                    when (before.type) {
                        is LayerType.MatMul -> step(output[index][b] as Double) *
                            (0 until after.size).sumOf { a -> delta[0][a] * weights[index][b][a] as Double }
                        is LayerType.Conv -> {
                            (output[index][b] as List<List<Double>>).sumOf {
                                it.sumOf { x ->
                                    step(x) * (0 until after.size).sumOf { a -> delta[0][a] * weights[index][b][a] as Double }
                                }
                            }
                        }
                    }
                }.let { delta.add(0, it) }
            }
        return delta
    }

    companion object {
        fun create(layers: List<LayerConfig>, random: Random, rate: Double): DevNetwork {
            val weights = mutableListOf<List<MutableList<Any>>>()
            layers
                .windowed(2) { (before, after) -> before to after }
                .map { (before, after) ->
                    (0 until before.size).map {
                        (0 until after.size).map {
                            when (after.type) {
                                is LayerType.MatMul -> random.nextDouble(from = -1.0, until = 1.0)
                                is LayerType.Conv -> (0..4).map { (0..4).map { random.nextDouble(-1.0, 1.0) } }
                            }
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
    val type: LayerType,
)

sealed class LayerType {
    object Input: LayerType()
    object MatMul : LayerType()
    object Conv : LayerType()
}
