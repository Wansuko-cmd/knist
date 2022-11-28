import common.mapDownIndexed
import common.maxIndex
import common.relu
import common.sigmoid
import common.step
import kotlin.random.Random

class Network(
    private val layers: List<Int>,
    private val weights: List<List<MutableList<Double>>>,
    private val rate: Double,
) {
    private val windowedLayers = layers.windowed(2) { (before, after) -> before to after }

    fun expect(input: List<Double>): Int {
        val output = forward(input)
        return (0 until layers.last()).map { output[layers.size - 1][it] }.maxIndex()
    }

    fun train(input: List<Double>, label: Int) {
        val output = forward(input)
        backward(output, calcDelta(output, label))
    }

    private fun forward(input: List<Double>): List<List<Double>> {
        val output = mutableListOf<List<Double>>()
        output.add(input)

        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until after).map { a ->
                    if (index == layers.size - 2) {
                        sigmoid((0 until before).sumOf { b -> output[index][b] * weights[index][b][a] })
                    } else {
                        relu((0 until before).sumOf { b -> output[index][b] * weights[index][b][a] })
                    }
                }.let { output.add(it) }
            }
        return output
    }

    private fun backward(
        output: List<List<Double>>,
        delta: List<List<Double>>,
    ) {
        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until before).forEach { b ->
                    (0 until after).forEach { a ->
                        weights[index][b][a] =
                            weights[index][b][a] - rate * delta[index + 1][a] * output[index][b]
                    }
                }
            }
    }

    private fun calcDelta(output: List<List<Double>>, label: Int): List<List<Double>> {
        val delta = mutableListOf<List<Double>>()
        (0 until layers.last()).map {
            val y = output[layers.size - 1][it]
            (y - if (it == label) 0.9 else 0.1) * (1 - y) * y
        }.let { delta.add(it) }
        windowedLayers
            .mapDownIndexed { index, (before, after) ->
                (0 until before).map { b ->
                    step(output[index][b]) *
                        (0 until after).sumOf { a -> delta[0][a] * weights[index][b][a] }
                }.let { delta.add(0, it) }
            }
        return delta
    }

    companion object {
        fun create(layers: List<Int>, random: Random, rate: Double): Network {
            val weights = mutableListOf<List<MutableList<Double>>>()
            layers
                .windowed(2) { (before, after) -> before to after }
                .map { (before, after) ->
                    (0 until before).map {
                        (0 until after).map {
                            random.nextDouble(from = -1.0, until = 1.0)
                        }.toMutableList()
                    }.let { weights.add(it) }
                }

            return Network(layers, weights, rate)
        }
    }
}
