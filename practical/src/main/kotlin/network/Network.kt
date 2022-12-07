package network

import common.maxIndex
import layers.IOType
import layers.LayerConfig
import layers.layer0d.Input0dConfig
import layers.layer0d.Output0dConfig
import layers.layer1d.Input1dConfig
import kotlin.random.Random

class Network(
    private val weights: Array<Array<IOType>>,
    private val output: Array<IOType>,
    private val delta: Array<Array<Double>>,
    private val forward: () -> Unit,
    private val calcDelta: (label: Int) -> Unit,
    private val backward: () -> Unit,
) {
    fun expect(input: List<Double>): Int {
        output[0] = input.toTypedArray().let { IOType.IOType0d(it) }
        forward()
        return output.last().asIOType0d().value.toList().maxIndex()
    }

    fun expects(input: List<List<Double>>): Int {
        output[0] = input.map { it.toTypedArray() }.toTypedArray().let { IOType.IOType1d(it) }
        forward()
        return output.last().asIOType0d().value.toList().maxIndex()
    }

    fun train(input: List<Double>, label: Int) {
        output[0] = input.toTypedArray().let { IOType.IOType0d(it) }
        forward()
        calcDelta(label)
        backward()
    }

    fun trains(input: List<List<Double>>, label: Int) {
        output[0] = input.map { it.toTypedArray() }.toTypedArray().let { IOType.IOType1d(it) }
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
        ): Network {
            val layers = listOf(inputConfig.toLayoutConfig()) + centerConfig + listOf(outputConfig.toLayoutConfig())
            return create(layers, random, rate)
        }

        fun create1d(
            inputConfig: Input1dConfig,
            centerConfig: List<LayerConfig<*>>,
            outputConfig: Output0dConfig,
            random: Random,
            rate: Double,
        ): Network {
            val layers = listOf(inputConfig.toLayoutConfig()) + centerConfig + listOf(outputConfig.toLayoutConfig())
            return create(layers, random, rate)
        }

        private fun create(
            layers: List<LayerConfig<*>>,
            random: Random,
            rate: Double,
        ): Network {
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
            )
        }
    }
}
