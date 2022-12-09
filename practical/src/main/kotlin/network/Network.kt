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
            val layers = listOf(inputConfig) + centerConfig + outputConfig.toLayer0dConfig()
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
            val layers = listOf(inputConfig) + centerConfig + outputConfig.toLayer0dConfig()
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
            // 前の層の出力（次の層の入力）の個数を数えるために利用
            var beforeOutput: IOType = IOType.IOType0d(arrayOf())
            val output: Array<IOType> = Array(layers.size) { i ->
                layers[i].createOutput(beforeOutput).also { beforeOutput = it }
            }
            val weights: Array<Array<IOType>> =
                Array(layers.size - 1) { i -> layers[i + 1].createWeight(output[i], random) }

            val delta: Array<Array<Double>> = Array(layers.size) { i ->
                // 最終層は delta = 教師信号とする
                layers.getOrElse(i) { layers.last() }
                    .createDelta(output.getOrElse(i - 1) { IOType.IOType0d(arrayOf()) })
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
                for (index in layers.size - 1 downTo 2) {
                    layers[index].type.calcDelta(
                        beforeDelta = delta[index - 1],
                        beforeOutput = output[index - 1],
                        delta = delta[index],
                        weight = weights.getOrElse(index - 1) { arrayOf() },
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
