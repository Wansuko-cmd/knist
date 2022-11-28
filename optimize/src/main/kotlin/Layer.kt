import common.mapDownIndexed
import common.maxIndex
import common.relu
import common.sigmoid
import common.step
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Network(
    private val layers: List<Int>,
    private val weights: MutableMap<WParam, Double>,
    private val rate: Double,
) {
    private val windowedLayers = layers.windowed(2) { (before, after) -> before to after }
    private val beforeAmountOfChange = mutableMapOf<Pair<Int, Int>, Double>()
    private val beforeRMSProp = mutableMapOf<Pair<Int, Int>, Double>()

    fun expect(input: List<Double>): Int {
        val output = forward(input)
        return (0 until layers.last()).map { output[layers.size - 1 to it]!! }.maxIndex()
    }

    fun train(input: List<Double>, label: Int) {
        val output = forward(input)
        backward(output, calcDelta(output, label))
    }

    private fun forward(input: List<Double>): Map<Pair<Int, Int>, Double> {
        val output = mutableMapOf<Pair<Int, Int>, Double>()
        (0 until layers.first()).forEach { output[0 to it] = input[it] }
        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until after).forEach { a ->
                    if (index == layers.size - 2) {
                        output[index + 1 to a] =
                            sigmoid((0 until before).sumOf { b -> output[index to b]!! * weights[WParam(index, b, a)]!! })
                    } else {
                        output[index + 1 to a] =
                            relu((0 until before).sumOf { b -> output[index to b]!! * weights[WParam(index, b, a)]!! })
                    }
                }
            }
        return output
    }

    private fun backward(
        output: Map<Pair<Int, Int>, Double>,
        delta: Map<Pair<Int, Int>, Double>,
    ) {
        windowedLayers
            .mapIndexed { index, (before, after) ->
                (0 until before).forEach { b ->
                    (0 until after).forEach { a ->
                        val g = delta[index + 1 to a]!! * output[index to b]!!
                        val amountOfChange =
                            0.9 * (beforeAmountOfChange[index + 1 to a] ?: 0.0) - rate * g
                        val rmsProp = (0.99 * (beforeRMSProp[index + 1 to a] ?: 0.0)) + 0.01 * g.pow(2)
                        weights[WParam(index, b, a)] =
                            weights[WParam(index, b, a)]!! + rate * (amountOfChange / sqrt(rmsProp + 1e-7))
                        beforeAmountOfChange[index + 1 to a] = amountOfChange
                        beforeRMSProp[index + 1 to a] = rmsProp
                    }
                }
            }
    }

    private fun calcDelta(output: Map<Pair<Int, Int>, Double>, label: Int): Map<Pair<Int, Int>, Double> {
        val teacher = MutableList(layers.last()) { 0.1 }
        teacher[label] = 0.9
        val delta = mutableMapOf<Pair<Int, Int>, Double>()
        (0 until layers.last()).forEach {
            val y = output[layers.size - 1 to it]
            delta[layers.size - 1 to it] = (y!! - teacher[it]) * (1 - y) * y
        }
        windowedLayers
            .mapDownIndexed { index, (before, after) ->
                (0 until before).forEach { b ->
                    delta[index to b] = step(output[index to b]!!) *
                        (0 until after).sumOf { a -> delta[index + 1 to a]!! * weights[WParam(index, b, a)]!! }
                }
            }
        return delta
    }

    companion object {
        fun create(layers: List<Int>, random: Random, rate: Double): Network {
            val weights: MutableMap<WParam, Double> = mutableMapOf()
            layers
                .windowed(2) { (before, after) -> before to after }
                .mapIndexed { index, (before, after) ->
                    (0 until before).forEach { b ->
                        (0 until after).forEach { a ->
                            weights[WParam(index, b, a)] = random.nextDouble(from = -1.0, until = 1.0)
                        }
                    }
                }

            return Network(layers, weights, rate)
        }
    }
}

data class WParam(val layer: Int, val from: Int, val to: Int)
