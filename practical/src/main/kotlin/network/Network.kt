package network

import common.maxIndex
import layers.IOType
import layers.LayerConfig
import layers.layer0d.Input0dConfig
import layers.layer0d.Output0dConfig
import layers.layer1d.Input1dConfig
import kotlin.random.Random

class Network<T>(
    private val weights: Array<Array<IOType>>,
    private val output: Array<IOType>,
    private val delta: Array<Array<Double>>,
    private val forward: () -> Unit,
    private val calcDelta: (label: Int) -> Unit,
    private val backward: () -> Unit,
    private val toIOType: T.() -> IOType,
) {
    fun expect(input: T): Int {
        output[0] = input.toIOType()
        forward()
        return output.last().asIOType0d().value.toList().maxIndex()
    }

    fun train(input: T, label: Int) {
        output[0] = input.toIOType()
        forward()
        calcDelta(label)
        backward()
    }

    companion object {
        fun create0d(
            inputConfig: Input0dConfig,
            centerConfig: List<LayerConfig<*>>,
            outputConfig: Output0dConfig,
            random: Random,
            rate: Double,
        ): Network<List<Double>> {
            val layers = listOf(inputConfig) + centerConfig + listOf(outputConfig.toLayer0dConfig())
            return create(
                layers = layers,
                random = random,
                rate = rate,
                toIOType = { IOType.IOType0d(this.toTypedArray()) },
            )
        }

        fun create1d(
            inputConfig: Input1dConfig,
            centerConfig: List<LayerConfig<*>>,
            outputConfig: Output0dConfig,
            random: Random,
            rate: Double,
        ): Network<List<List<Double>>> {
            val layers = listOf(inputConfig.toLayoutConfig()) + centerConfig + listOf(outputConfig.toLayer0dConfig())
            return create(
                layers = layers,
                random = random,
                rate = rate,
                toIOType = { IOType.IOType1d(this.map { it.toTypedArray() }.toTypedArray()) },
            )
        }

        private fun <T> create(
            layers: List<LayerConfig<*>>,
            random: Random,
            rate: Double,
            toIOType: T.() -> IOType,
        ): Network<T> {
            // 値を全てバラバラにするために分割
            val weights: Array<Array<IOType>> =
                Array(layers.size - 1) { i ->
                    Array(layers[i].numOfOutput) { layers[i + 1].createWeight(random) }
                }

            val output: Array<IOType> = Array(layers.size) { i -> layers[i].createOutput() }
            val delta: Array<Array<Double>> = Array(layers.size + 1) { i ->
                Array(layers.getOrElse(i) { layers.last() }.numOfNeuron) { 0.0 }
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
                toIOType = toIOType,
            )
        }
    }
}
