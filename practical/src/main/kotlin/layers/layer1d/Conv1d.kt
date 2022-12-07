@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package layers.layer1d

import common.step
import layers.IOType
import layers.LayerType

object Conv1d : LayerType {
    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun forward(
        input: IOType,
        output: IOType,
        weight: Array<IOType>,
        activationFunction: (Double) -> Double,
    ) {
        val inputArray = input.asIOType1d().value
        val outputArray = output.asIOType1d().value
        for (outputChannel in outputArray.indices) {
            for (t in outputArray[outputChannel].indices) {
                outputArray[outputChannel][t] = 0.0
            }
            for (inputChannel in inputArray.indices) {
                inputArray[inputChannel].conv1d(
                    kernel = weight[inputChannel].asIOType1d().value[outputChannel],
                    output = outputArray[outputChannel],
                    activationFunction = activationFunction,
                )
            }
        }
    }

    override inline fun calcDelta(
        delta: Array<Double>,
        output: IOType,
        afterDelta: Array<Double>,
        afterWeight: Array<IOType>,
    ) {
        // 畳み込みの出力ニューロンを一列にした時のindexを表す
        var index = 0
        val outputArray = output.asIOType1d().value

        for (i in delta.indices) {
            var sum = 0.0
            val afterWeightArray = afterWeight[i].asIOType0d().value
            for (t in outputArray[i].indices) {
                sum += step(outputArray[i][t]) * (0 until afterWeight[index++].asIOType0d().value.size)
                    .sumOf { afterDelta[it] * afterWeightArray[it] }
            }
            delta[i] = sum
        }
    }

    /**
     * weight: Array[入力チャンネル][出力チャンネル][kernelの横要素]
     */
    override inline fun backward(
        weight: Array<IOType>,
        delta: Array<Double>,
        input: IOType,
        rate: Double,
    ) {
        val inputArray = input.asIOType1d().value
        for (inputChannel in weight.indices) {
            val weightArray = weight[inputChannel].asIOType1d().value
            for (outputChannel in weightArray.indices) {
                for (t in weightArray[outputChannel].indices) {
                    var sum = 0.0
                    val kernelSize = inputArray[inputChannel].size - weightArray[outputChannel].size + 1
                    for (outputIndex in 0 until kernelSize) {
                        sum += inputArray[inputChannel][t + outputIndex]
                    }
                    weightArray[outputChannel][t] -= rate * delta[outputChannel] * sum
                }
            }
        }
    }
}

inline fun Array<Double>.conv1d(
    kernel: Array<Double>,
    output: Array<Double>,
    activationFunction: (Double) -> Double,
) {
    for (outputIndex in output.indices) {
        var sum = 0.0
        for (kernelIndex in kernel.indices) {
            sum += this[outputIndex + kernelIndex] * kernel[kernelIndex]
        }
        output[outputIndex] += activationFunction(sum)
    }
}
