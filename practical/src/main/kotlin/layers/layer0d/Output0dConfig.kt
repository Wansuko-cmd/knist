@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.layer0d

import common.sigmoid
import layers.IOType
import layers.LayerType
import kotlin.math.exp

sealed interface Output0dConfig {

    fun toLayer0dConfig(): List<Layer0dConfig>

    data class Sigmoid(val size: Int, val type: LayerType) : Output0dConfig {
        override fun toLayer0dConfig() =
            listOf(
                Layer0dConfig(
                    numOfNeuron = size,
                    activationFunction = ::sigmoid,
                    type = type,
                ),
                Layer0dConfig(
                    numOfNeuron = size,
                    activationFunction = { it },
                    type = object : LayerType {
                        override inline fun forward(
                            input: IOType,
                            output: IOType,
                            weight: Array<IOType>,
                            activationFunction: (Double) -> Double,
                        ) {
                            val inputArray = input.asIOType0d().value
                            val outputArray = output.asIOType0d().value
                            for (i in inputArray.indices) {
                                outputArray[i] = inputArray[i]
                            }
                        }

                        override inline fun calcDelta(
                            beforeDelta: Array<Double>,
                            beforeOutput: IOType,
                            delta: Array<Double>,
                            weight: Array<IOType>,
                        ) {
                            val beforeOutputArray = beforeOutput.asIOType0d().value
                            for (i in beforeDelta.indices) {
                                val y = beforeOutputArray[i]
                                beforeDelta[i] = (y - delta[i]) * (1 - y) * y
                            }
                        }

                        override inline fun backward(
                            weight: Array<IOType>,
                            delta: Array<Double>,
                            input: IOType,
                            rate: Double,
                        ) = Unit
                    },
                ),
            )
    }

    data class Softmax(val size: Int, val type: LayerType) : Output0dConfig {
        override fun toLayer0dConfig() =
            listOf(
                Layer0dConfig(
                    numOfNeuron = size,
                    activationFunction = { it },
                    type = type,
                ),
                Layer0dConfig(
                    numOfNeuron = size,
                    activationFunction = { it },
                    type = object : LayerType {
                        override inline fun forward(
                            input: IOType,
                            output: IOType,
                            weight: Array<IOType>,
                            activationFunction: (Double) -> Double,
                        ) {
                            val inputArray = input.asIOType0d().value
                            val outputArray = output.asIOType0d().value
                            val max = inputArray.max()
                            val exp = inputArray.map { exp(it - max) }
                            val sum = exp.sum()
                            for (inputIndex in inputArray.indices) {
                                inputArray[inputIndex] = exp[inputIndex] / sum
                                outputArray[inputIndex] = inputArray[inputIndex]
                            }
                        }

                        override inline fun calcDelta(
                            beforeDelta: Array<Double>,
                            beforeOutput: IOType,
                            delta: Array<Double>,
                            weight: Array<IOType>,
                        ) {
                            val outputArray = beforeOutput.asIOType0d().value
                            for (i in beforeDelta.indices) {
                                val q = if (delta[i] > 0.5) 1.0 else 0.0
                                val y = outputArray[i]
                                beforeDelta[i] = y - q
                            }
                        }

                        override inline fun backward(
                            weight: Array<IOType>,
                            delta: Array<Double>,
                            input: IOType,
                            rate: Double,
                        ) = Unit
                    },
                ),
            )
    }
}
