import common.mapDownIndexed
import common.maxIndex
import common.relu
import common.sigmoid
import common.step
import kotlin.random.Random

class Network(
    private val layers: List<Int>,
    private val weights: MutableMap<WParam, Double>,
) {
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
        layers
            .windowed(2) { (before, after) -> before to after }
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

    private fun backward(output: Map<Pair<Int, Int>, Double>, delta: Map<Pair<Int, Int>, Double>) {
        layers
            .windowed(2) { (before, after) -> before to after }
            .mapIndexed { index, (before, after) ->
                (0 until before).forEach { b ->
                    (0 until after).forEach { a ->
                        weights[WParam(index, b, a)] =
                            weights[WParam(index, b, a)]!! - 0.01 * delta[index + 1 to a]!! * output[index to b]!!
                    }
                }
            }
    }

    private fun calcDelta(output: Map<Pair<Int, Int>, Double>, label: Int): Map<Pair<Int, Int>, Double> {
        val teacher = MutableList(layers.last()) { 0.0 }
        teacher[label] = 1.0
        val delta = mutableMapOf<Pair<Int, Int>, Double>()
        (0 until layers.last()).forEach {
            val y = output[layers.size - 1 to it]
            delta[layers.size - 1 to it] = (y!! - teacher[it]) * (1 - y) * y
        }
        layers
            .windowed(2) { (before, after) -> before to after }
            .mapDownIndexed { index, (before, after) ->
                (0 until before).forEach { b ->
                    delta[index to b] = step(output[index to b]!!) *
                        (0 until after).sumOf { a -> delta[index + 1 to a]!! * weights[WParam(index, b, a)]!! }
                }
            }
        return delta
    }

    companion object {
        fun create(layers: List<Int>, random: Random): Network {
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

            return Network(layers, weights)
        }
    }
}

data class WParam(val layer: Int, val from: Int, val to: Int)
