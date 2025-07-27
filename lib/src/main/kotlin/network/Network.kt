package network

import common.iotype.IOType
import common.iotype.IOType0d
import common.iotype.IOType1d
import common.iotype.IOType2d
import common.maxIndex
import layers.Layer
import layers.input.Input0dLayer
import layers.input.Input1dLayer
import layers.input.Input2dLayer
import layers.output.layer0d.Output0dLayer
import kotlin.math.absoluteValue
import kotlin.random.Random

class Network<T>(
    private val weights: Array<Array<IOType>>,
    private val output: Array<IOType>,
    private val delta: Array<IOType>,
    private val forward: () -> Unit,
    private val calcDelta: (label: Int) -> Unit,
    private val backward: () -> Unit,
    private val toIOType: T.() -> IOType,
) {
    var lossValue = 0.0 to 0

    fun expect(input: T): Int {
        output[0] = input.toIOType()
        forward()
        return output.last().asIOType0d().inner.toList().maxIndex()
    }

    fun train(input: T, label: Int) {
        output[0] = input.toIOType()
        forward()
        calcDelta(label)
        backward()
        lossValue =
            lossValue.first + delta[delta.lastIndex - 1].asIOType0d().inner.map { it.absoluteValue }.average() to lossValue.second + 1
    }

    fun loss() = lossValue
        .let { it.first / it.second }
        .also { lossValue = 0.0 to 0 }

    companion object {
        fun create0d(
            inputConfig: Input0dLayer,
            centerConfig: List<Layer<*>>,
            outputConfig: Output0dLayer,
            random: Random,
            rate: Double,
        ): Network<List<Double>> {
            val layers = listOf(inputConfig) + centerConfig + outputConfig.toLayer()
            return create(
                layers = layers,
                random = random,
                rate = rate,
                toIOType = { IOType0d(this.toMutableList()) },
            )
        }

        fun create1d(
            inputConfig: Input1dLayer,
            centerConfig: List<Layer<*>>,
            outputConfig: Output0dLayer,
            random: Random,
            rate: Double,
        ): Network<List<List<Double>>> {
            val layers = listOf(inputConfig) + centerConfig + outputConfig.toLayer()
            return create(
                layers = layers,
                random = random,
                rate = rate,
                toIOType = { IOType1d.create(this.map { it.toMutableList() }.toMutableList()) },
            )
        }

        fun create2d(
            inputConfig: Input2dLayer,
            centerConfig: List<Layer<*>>,
            outputConfig: Output0dLayer,
            random: Random,
            rate: Double,
        ): Network<List<List<List<Double>>>> {
            val layers = listOf(inputConfig) + centerConfig + outputConfig.toLayer()
            return create(
                layers = layers,
                random = random,
                rate = rate,
                toIOType = {
                    IOType2d(
                        this.map { channel ->
                            channel.map { row ->
                                row.toDoubleArray()
                            }.toTypedArray()
                        }.toTypedArray(),
                    )
                },
            )
        }

        private fun <T> create(
            layers: List<Layer<*>>,
            random: Random,
            rate: Double,
            toIOType: T.() -> IOType,
        ): Network<T> {
            // 前の層の出力（次の層の入力）の個数を数えるために利用
            var beforeOutput: IOType = IOType0d(mutableListOf())
            val output: Array<IOType> = Array(layers.size) { i ->
                layers[i].createOutput(beforeOutput).also { beforeOutput = it }
            }
            val weights: Array<Array<IOType>> =
                Array(layers.size - 1) { i -> layers[i + 1].createWeight(output[i], random) }

            val delta: Array<IOType> = Array(layers.size) { i ->
                // 最終層は delta = 教師信号とする
                layers.getOrElse(i) { layers.last() }
                    .createDelta(output.getOrElse(i - 1) { IOType0d(mutableListOf()) })
            }
            val forward = {
                for (index in 0 until layers.size - 1) {
                    layers[index + 1].forward(
                        input = output[index],
                        output = output[index + 1],
                        weight = weights[index],
                    )
                }
            }

            val calcDelta = { label: Int ->
                val deltaArray = delta.last().asIOType0d()
                deltaArray.inner.fill(0.0)
                deltaArray[label] = 1.0
                for (index in layers.size - 1 downTo 2) {
                    layers[index].calcDelta(
                        beforeDelta = delta[index - 1],
                        beforeOutput = output[index - 1],
                        delta = delta[index],
                        weight = weights.getOrElse(index - 1) { arrayOf() },
                    )
                }
            }

            val backward = {
                for (index in 0 until layers.size - 1) {
                    layers[index + 1].backward(
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
