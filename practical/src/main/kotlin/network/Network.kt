package network

import common.maxIndex
import layers.affine.backward
import layers.affine.calcDelta
import layers.affine.calcLastDelta
import layers.affine.forward
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
            input: InputConfig,
            config: List<LayerConfig>,
            random: Random,
            rate: Double,
        ): Network {
            val layers = listOf(input.toLayoutConfig()) + config

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
            val delta: Array<Array<Double>> = Array(layers.size) { i -> Array(layers[i].size) { 0.0 } }
            val forward = {
                for (index in 0 until layers.size - 1) {
                    forward(output[index], output[index + 1], weights[index], layers[index + 1].activationFunction)
                }
            }

            val calcDelta = { label: Int ->
                calcLastDelta(delta[layers.size - 1], output[layers.size - 1], label)
                for (index in layers.size - 2 downTo 1) {
                    calcDelta(delta[index], output[index], delta[index + 1], weights[index])
                }
            }

            val backward = {
                for (index in 0 until layers.size - 1) {
                    backward(weights[index], delta[index + 1], output[index], rate)
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
