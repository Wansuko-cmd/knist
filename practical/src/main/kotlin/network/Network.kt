package network

import common.maxIndex
import layers.InputConfig
import layers.LayerConfig
import layers.OutputConfig
import kotlin.random.Random

class Network(
    private val weights: Array<Array<Array<Double>>>,
    private val output: Array<Array<Double>>,
    private val delta: Array<Array<Double>>,
    private val forward: () -> Unit,
    private val calcDelta: (label: Int) -> Unit,
    private val backward: () -> Unit,
) {
    fun expect(input: List<Double>): Int {
        output[0] = input.toTypedArray()
        forward()
        return (0 until output.last().size).map { output.last()[it] }.maxIndex()
    }

    fun train(input: List<Double>, label: Int) {
        output[0] = input.toTypedArray()
        forward()
        calcDelta(label)
        backward()
    }

    companion object {
        fun create(
            inputConfig: InputConfig,
            centerConfig: List<LayerConfig>,
            outputConfig: OutputConfig,
            random: Random,
            rate: Double,
        ): Network {
            val layers = listOf(inputConfig.toLayoutConfig()) + centerConfig + listOf(outputConfig.toLayoutConfig())

            // 値を全てバラバラにするために分割
            val weights: Array<Array<Array<Double>>> =
                Array(layers.size - 1) { i ->
                    Array(layers[i].size) { Array(layers[i + 1].size) { 0.0 } }
                }
            layers
                .windowed(2) { (before, after) -> before to after }
                .mapIndexed { index, (before, after) ->
                    for (b in 0 until before.size) {
                        for (a in 0 until after.size) {
                            weights[index][b][a] = random.nextDouble(-1.0, 1.0)
                        }
                    }
                }

            val output: Array<Array<Double>> = Array(layers.size) { i -> Array(layers[i].size) { 0.0 } }
            val delta: Array<Array<Double>> = Array(layers.size + 1) { i ->
                Array(layers.getOrElse(i) { layers.last() }.size) { 0.0 }
            }
            val forward = {
                for (index in 0 until layers.size - 1) {
                    layers[index + 1].type.forward(
                        input = output[index],
                        output = output[index + 1],
                        weight = weights[index],
                        activationFunction = layers[index + 1].activationFunction,
                    )
                }
            }

            val calcDelta = { label: Int ->
                for (index in delta.last().indices) {
                    delta.last()[index] = if (index == label) 0.9 else 0.1
                }
                for (index in layers.size - 1 downTo 1) {
                    layers[index].type.calcDelta(
                        delta = delta[index],
                        output = output[index],
                        afterDelta = delta[index + 1],
                        afterWeight = weights.getOrElse(index) { arrayOf() },
                    )
                }
            }

            val backward = {
                for (index in 0 until layers.size - 1) {
                    layers[index + 1].type.backward(
                        weight = weights[index],
                        delta = delta[index + 1],
                        input = output[index],
                        rate = rate,
                    )
                }
            }

            return Network(
                weights = weights,
                output = output,
                delta = delta,
                forward = forward,
                calcDelta = calcDelta,
                backward = backward,
            )
        }
    }
}
